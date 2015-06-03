package com.socialreader.ui;

import com.socialreader.core.GooglePersonFinder;
import com.socialreader.core.Profile;
import com.socialreader.core.ProfileBuilder;
import com.socialreader.input.InputReader;
import com.socialreader.output.CsvOutputWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public ComboBox<String> maxResults;
    public TableView<Profile> peopleTable;
    public TableColumn<Profile, String> firstNameColumn;
    public TableColumn<Profile, String> lastNameColumn;
    public TableColumn<Profile, String> linkedIn;
    public TableColumn<Profile, String> title;
    public TableColumn<Profile, String> currentEmployer;
    public TableColumn<Profile, String> location;
    public TableColumn<Profile, String> industry;

    public ProgressIndicator progressIndicator;
    public CheckBox pingEmail;
    private List<Profile> profiles = new LinkedList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Controller initialing");
        bindService();
        initializeColumns();

        peopleTable.setRowFactory(tv -> {
            TableRow<Profile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Profile profile = row.getItem();
                    GooglePersonFinder.openWebPage(profile.getLinkedInUrl());
                }
            });
            return row;
        });

        LOGGER.debug("Controller initialized");
    }

    private void initializeColumns() {
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
        title.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        linkedIn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLinkedInUrl()));
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
        fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV File", "*.csv"));
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

    public void google() throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
        final GooglePersonFinder finder = new GooglePersonFinder(getInputReader());
        finder.openWebPage();
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
                    final GooglePersonFinder finder = new GooglePersonFinder(getInputReader());

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

                private Optional<Profile> processProfile(ProfileBuilder profileBuilder) {
                    try {
                        profileBuilder.initWebsiteScrapers();
                        profileBuilder.generateProfileFromWebsiteScrapers();
                        if (pingEmail.isSelected()) {
                            profileBuilder.initEmailResolver();
                        }
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

    private InputReader getInputReader() {
        final InputReader reader = new InputReader(Integer.valueOf(maxResults.getValue()));
        if (!jobTitles.getText().isEmpty()) {
            reader.getTitles().addAll(splitString(jobTitles));
        }
        if (!locations.getText().isEmpty()) {
            reader.getLocations().addAll(splitString(locations));
        }
        if (!industries.getText().isEmpty()) {
            reader.getIndustries().addAll(splitString(industries));
        }
        if (!keywords.getText().isEmpty()) {
            reader.getKeyWords().addAll(splitString(keywords));
        }
        return reader;
    }

    private Set<String> splitString(TextField multiValue) {
        return Stream.of(multiValue.getText().split(",")).map(String::trim).collect(Collectors.toSet());
    }

}
