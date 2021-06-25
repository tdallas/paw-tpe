package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.*;
import ar.edu.itba.paw.interfaces.dtos.*;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.ChargeService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.charge.Charge;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.help.Help;
import ar.edu.itba.paw.models.product.Product;
import ar.edu.itba.paw.models.reservation.Calification;
import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.models.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    public static final int GENERATED_PASSWORD_LENGTH = 8;

    private final ProductDao productDao;
    private final ChargeDao chargeDao;
    private final ReservationDao reservationDao;
    private final UserDao userDao;
    private final HelpDao helpDao;
    private final ChargeService chargeService;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(ProductDao productDao, ChargeDao chargeDao, ReservationDao reservationDao,
                           UserDao userDao, HelpDao helpDao, ChargeService chargeService, EmailService emailService) {
        this.productDao = productDao;
        this.chargeDao = chargeDao;
        this.reservationDao = reservationDao;
        this.userDao = userDao;
        this.helpDao = helpDao;
        this.chargeService = chargeService;
        this.emailService = emailService;
    }

    @Override
    public PaginatedDTO<ProductResponse> getProducts(int page, int pageSize) {
        if (pageSize < 1 || page < 1) throw new IndexOutOfBoundsException("Pagination requested invalid.");
        PaginatedDTO<Product> products = productDao.findAllActive(page, pageSize);
        return new PaginatedDTO<>(products.getList()
                .stream().map(ProductResponse::fromProduct).collect(Collectors.toList()), products.getMaxItems());
    }

    @Override
    @Transactional
    public List<ActiveReservationResponse> findActiveReservations(final String userEmail) {
        return reservationDao.findActiveReservationsByEmail(userEmail)
                .stream().map(ActiveReservationResponse::fromReservation).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse getUserById(long userId) throws EntityNotFoundException {
        Optional<User> possibleUser = userDao.findById(userId);
        if (possibleUser.isPresent()) {
            return UserResponse.fromUser(possibleUser.get());
        }
        throw new EntityNotFoundException("Can't find a user with id " + userId);
    }

    @Override
    public List<ChargesByUserResponse> checkProductsPurchasedByUserByReservationId(String userEmail, long reservationId)
        throws EntityNotFoundException {
        Map<Product, Integer> productToQtyMap = chargeDao.getAllChargesByUser(userEmail, reservationId);
        return productToQtyMap.keySet().stream().map(
                product -> new ChargesByUserResponse(
                        product.getDescription(), product.getId(), product.getPrice(), productToQtyMap.get(product))
        ).collect(Collectors.toList());
    }

    @Override
    public Charge addCharge(long productId, long reservationId) throws EntityNotFoundException {
        Product product = productDao.findById(productId).orElseThrow(
                () -> new EntityNotFoundException("Can't find product with id " + productId));
        Reservation reservation = reservationDao.findById(reservationId).orElseThrow(
                () -> new EntityNotFoundException("Can't find reservation with id " + reservationId));
        return chargeDao.save(new Charge(product, reservation));
    }

    @Override
    public User getUserForReservation(String userEmail) {
        User user;
        Optional<User> userOptional = userDao.findByEmail(userEmail);
        if (userOptional.isPresent()) {
            user = userOptional.get();
            LOGGER.info("There is already an user created with email " + userEmail);
        } else {
            LOGGER.info("There is no user created with email " + userEmail + ". So we'll create one.");
            String randomPassword = generatePassword();
            user = userDao.save(new User(userEmail, userEmail, new BCryptPasswordEncoder().encode(randomPassword)));
            LOGGER.info("User created! Sending e-mail about user creation to: " + userEmail);
            emailService.sendUserCreatedEmail(userEmail, randomPassword);
        }
        return user;
    }

    @Override
    public HelpResponse requestHelp(String text, long reservationId) throws EntityNotFoundException {
        Reservation reservation = reservationDao.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find reservation"));
        if (text.length() > 0 && isValidString(text)) {
            return HelpResponse.fromHelpRequest(helpDao.save(new Help(text, reservation)));
        }
        return null;
    }

    @Transactional
    @Override
    public void rateStay(String rate, String reservationHash)
            throws EntityNotFoundException, RequestInvalidException {
        Reservation reservation = reservationDao.findReservationByHash(reservationHash)
                .orElseThrow(() -> new EntityNotFoundException("Reservation was not found"));
        if (reservation.getCalification() != null) {
            throw new RequestInvalidException();
        }
        reservationDao.rateStay(reservation.getId(), transformRate(rate));
    }

    @Override
    @Transactional
    public HelpResponse getHelpRequestById(long reservationId, long helpId) throws EntityNotFoundException {
        Optional<Help> possibleHelp = helpDao.findById(helpId);
        if (!possibleHelp.isPresent()) {
            throw new EntityNotFoundException("Help request not found.");
        }
        Help helpRequest = possibleHelp.get();
        if (helpRequest.getReservation().getId() != reservationId) {
            throw new EntityNotFoundException("Reservation does not belong to help request.");
        }
        return HelpResponse.fromHelpRequest(helpRequest);
    }

    @Override
    public ChargeDeliveryResponse getCharge(long chargeId) throws EntityNotFoundException {
        return chargeService.getChargeById(chargeId);
    }

    private String transformRate(String rate) {
        if (isInt(rate)) {
            return Calification.values()[Integer.parseInt(rate) - 1].name();
        }
        return rate.toUpperCase();
    }

    private boolean isValidString(String text) {
        return text.matches("^.*[a-zA-Z0-9áéíóúüñÁÉÍÓÚÑ ].*$");
    }

    private boolean isInt(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private Integer[] generateRandomIntsArray() {
        Random random = new SecureRandom();
        Integer[] ints = new Integer[GENERATED_PASSWORD_LENGTH];
        for (int i = 0; i < GENERATED_PASSWORD_LENGTH; i++) {
            ints[i] = random.nextInt(26);
        }
        return ints;
    }

    protected String generatePassword() {
        LOGGER.info("Generating password...");
        Integer[] ints = generateRandomIntsArray();
        StringBuilder password = new StringBuilder(GENERATED_PASSWORD_LENGTH);
        for (Integer i : ints) {
            password.append(Character.toChars('a' + i));
        }
        return password.toString();
    }
}
