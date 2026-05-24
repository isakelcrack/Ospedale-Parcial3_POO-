package app;

import controller.AppointmentController;
import controller.AuthController;
import controller.DoctorController;
import controller.HospitalizationController;
import controller.PatientController;
import controller.PrescriptionController;
import controller.TableDataController;
import java.nio.file.Path;
import repository.AppointmentRepository;
import repository.HospitalizationRepository;
import repository.InMemoryAppointmentRepository;
import repository.InMemoryHospitalizationRepository;
import repository.InMemoryUserRepository;
import repository.JsonUserLoader;
import repository.ModelEventPublisher;
import repository.UserRepository;
import service.AppointmentService;
import service.AuthService;
import service.DoctorService;
import service.HospitalizationService;
import service.PatientService;
import service.PrescriptionService;
import service.TableDataService;
import service.ValidationService;

public class ApplicationContext {

    private final ModelEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalizationRepository hospitalizationRepository;

    private final AuthController authController;
    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final HospitalizationController hospitalizationController;
    private final PrescriptionController prescriptionController;
    private final TableDataController tableDataController;

    private ApplicationContext() {
        this.eventPublisher = new ModelEventPublisher();
        this.userRepository = new InMemoryUserRepository(eventPublisher);
        this.appointmentRepository = new InMemoryAppointmentRepository(eventPublisher);
        this.hospitalizationRepository = new InMemoryHospitalizationRepository(eventPublisher);

        ValidationService validationService = new ValidationService(userRepository);
        AuthService authService = new AuthService(userRepository);
        PatientService patientService = new PatientService(userRepository, validationService);
        DoctorService doctorService = new DoctorService(userRepository, validationService);
        AppointmentService appointmentService = new AppointmentService(
                appointmentRepository, userRepository, validationService);
        HospitalizationService hospitalizationService = new HospitalizationService(
                hospitalizationRepository, appointmentRepository, userRepository, validationService);
        PrescriptionService prescriptionService = new PrescriptionService(appointmentRepository, validationService);
        TableDataService tableDataService = new TableDataService(
                userRepository, appointmentRepository, hospitalizationRepository);

        this.authController = new AuthController(authService);
        this.patientController = new PatientController(patientService);
        this.doctorController = new DoctorController(doctorService);
        this.appointmentController = new AppointmentController(appointmentService);
        this.hospitalizationController = new HospitalizationController(hospitalizationService);
        this.prescriptionController = new PrescriptionController(prescriptionService);
        this.tableDataController = new TableDataController(tableDataService);
    }

    public static ApplicationContext bootstrap() {
        ApplicationContext context = new ApplicationContext();
        new JsonUserLoader(context.userRepository).load(Path.of("json", "users.json"));
        return context;
    }

    public ModelEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public AuthController getAuthController() {
        return authController;
    }

    public PatientController getPatientController() {
        return patientController;
    }

    public DoctorController getDoctorController() {
        return doctorController;
    }

    public AppointmentController getAppointmentController() {
        return appointmentController;
    }

    public HospitalizationController getHospitalizationController() {
        return hospitalizationController;
    }

    public PrescriptionController getPrescriptionController() {
        return prescriptionController;
    }

    public TableDataController getTableDataController() {
        return tableDataController;
    }
}
