package com.socialreader.ui;

import com.socialreader.core.GooglePersonFinder;
import com.socialreader.core.Profile;
import com.socialreader.core.ProfileBuilder;
import com.socialreader.input.DummyInputReader;
import com.socialreader.input.InputReader;
import com.socialreader.output.CsvOutputWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MainController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private final GetProfilesService service = new GetProfilesService();
    private final AtomicBoolean doRefreshData = new AtomicBoolean(true);


    public TextField firstName;
    public TextField lastName;
    public TextField jobTitles;
    public TextField companies;
    public TextField schools;
    public TextField locations;
    public TextField industries;
    public TextField keywords;
    public TableView<Profile> peopleTable;
    public TableColumn<Profile, String> firstNameColumn;
    public TableColumn<Profile, String> lastNameColumn;
    public TableColumn<Profile, String> title;
    public TableColumn<Profile, String> currentEmployer;
    public TableColumn<Profile, String> location;
    public TableColumn<Profile, String> industry;

    public ProgressIndicator progressIndicator;
    private List<Profile> profiles = new LinkedList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Controller initialing");
        bindService();
        initializeColumns();
        LOGGER.debug("Controller initialized");
    }

    private void initializeColumns() {
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
        title.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        currentEmployer.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCurrentEmployer()));
        location.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));
        industry.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndustry()));
    }

    public void search() {
        service.restart();
    }

    public void export() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            new CsvOutputWriter(file.getPath()).writeProfileInformation(profiles);
        }

    }

    private void bindService() {
        progressIndicator.setMaxSize(150, 150);
        progressIndicator.progressProperty().bind(service.progressProperty());
        progressIndicator.visibleProperty().bind(service.runningProperty());
        peopleTable.itemsProperty().bind(service.valueProperty());
        service.setOnRunning(e -> peopleTable.setPlaceholder(new Text("\n\nSearching...")));
    }

    private class GetProfilesService extends Service<ObservableList<Profile>> {
        @Override
        protected Task<ObservableList<Profile>> createTask() {
            return new Task<ObservableList<Profile>>() {
                @Override
                protected ObservableList<Profile> call() throws Exception {
                    if (!doRefreshData.get()) return FXCollections.emptyObservableList();
                    return getProfiles();
                }

                private ObservableList<Profile> getProfiles() throws InterruptedException {
                    final GooglePersonFinder finder = new GooglePersonFinder();
//                    finder.configureSearch(new DummyInputReader());
                    finder.configureSearch(getInputReader());

                    profiles.clear();
                    final Set<ProfileBuilder> builders = finder.generateProfileBuilders();
                    final int size = builders.size();

                    final AtomicInteger counter = new AtomicInteger();
                    builders.parallelStream().forEach(b -> {
                        processProfile(b).ifPresent(profiles::add);
                        final int currentCount = counter.incrementAndGet();
                        updateProgress(currentCount, size);
                        LOGGER.debug("Processed profile {} {}/{}", b.getProfile(), currentCount, size);
                    });

                    return FXCollections.observableList(profiles);
                }

                private InputReader getInputReader() {
                    final InputReader reader = new InputReader() {
                    };
                    reader.setFirstName(firstName.getText());
                    reader.setLastName(lastName.getText());
                    if (!jobTitles.getText().isEmpty()) {
                        reader.getTitles().add(jobTitles.getText());
                    }
                    if (!companies.getText().isEmpty()) {
                        reader.getCompanies().add(companies.getText());
                    }
                    if (!schools.getText().isEmpty()) {
                        reader.getSchools().add(schools.getText());
                    }
                    if (!locations.getText().isEmpty()) {
                        reader.getLocations().add(locations.getText());
                    }
                    if (!industries.getText().isEmpty()) {
                        reader.getIndustries().add(industries.getText());
                    }
                    if (!keywords.getText().isEmpty()) {
                        reader.getKeyWords().add(keywords.getText());
                    }
                    return reader;
                }

                private Optional<Profile> processProfile(ProfileBuilder profileBuilder) {
                    try {
                        profileBuilder.initWebsiteScrapers();
                        profileBuilder.generateProfileFromWebsiteScrapers();
                        //profileBuilder.initEmailResolver();
                        //profileBuilder.enrichProfileWithEmailResolver();
                        return Optional.of(profileBuilder.getProfile());
                    } catch (Exception x) {
                        LOGGER.error("Can't process profile {}", profileBuilder.getProfile(), x);
                        return Optional.empty();
                    }
                }

            };
        }
    }

}
