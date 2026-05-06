package com.project.hotel.service;

import com.project.hotel.entity.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmationEmail(Booking booking) {

        String toEmail = booking.getUser().getEmail();

        log.info("Booking confirmation email sending started: bookingId={}, to={}",
                booking.getId(), toEmail);

        String subject = "Hotel Booking Confirmed";

        String body =
                "Hello " + booking.getUser().getName() + ",\n\n" +
                        "Your hotel booking has been confirmed.\n\n" +
                        "Booking Details:\n" +
                        "Booking ID: " + booking.getId() + "\n" +
                        "Room Number: " + booking.getRoom().getRoomNumber() + "\n" +
                        "Check-in: " + booking.getCheckIn() + "\n" +
                        "Check-out: " + booking.getCheckOut() + "\n\n" +
                        "Thank you for choosing our hotel.\n\n" +
                        "Regards,\n" +
                        "Hotel Management Team";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try {

            mailSender.send(message);

            log.info("Booking confirmation email sent successfully: bookingId={}, to={}",
                    booking.getId(), toEmail);

        } catch (Exception ex) {

            log.error("Failed to send booking confirmation email: bookingId={}, to={}, error={}",
                    booking.getId(),
                    toEmail,
                    ex.getMessage());

            throw ex;
        }
    }

    public void sendBookingCancellationEmail(Booking booking) {

        String toEmail = booking.getUser().getEmail();

        log.info("Booking cancellation email sending started: bookingId={}, to={}",
                booking.getId(), toEmail);

        String subject = "Hotel Booking Cancelled";

        String body =
                "Hello " + booking.getUser().getName() + ",\n\n" +
                        "Your hotel booking has been cancelled successfully.\n\n" +
                        "Cancelled Booking Details:\n" +
                        "Booking ID: " + booking.getId() + "\n" +
                        "Room Number: " + booking.getRoom().getRoomNumber() + "\n" +
                        "Check-in: " + booking.getCheckIn() + "\n" +
                        "Check-out: " + booking.getCheckOut() + "\n\n" +
                        "We hope to serve you again.\n\n" +
                        "Regards,\n" +
                        "Hotel Management Team";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try {

            mailSender.send(message);

            log.info("Booking cancellation email sent successfully: bookingId={}, to={}",
                    booking.getId(), toEmail);

        } catch (Exception ex) {

            log.error("Failed to send booking cancellation email: bookingId={}, to={}, error={}",
                    booking.getId(),
                    toEmail,
                    ex.getMessage());

            throw ex;
        }
    }
}