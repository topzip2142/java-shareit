package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> createBooking(NewBookingRequest booking, long userId) {
        return post("", userId, booking);
    }

    public ResponseEntity<Object> approveBooking(long userId, long bookingId, boolean approved) {
        String path = "/" + bookingId + "?approved={approved}";

        Map<String, Object> parameters = Map.of(
                "approved", approved
        );

        return patch(path, userId, parameters, null);
    }

    public ResponseEntity<Object> getAllBookings(long userId, String state) {
        String path = "?state={state}";

        Map<String, Object> parameters = Map.of(
                "state", state
        );

        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(long userId, String state) {
        String path = "/owner?state={state}";

        Map<String, Object> parameters = Map.of(
                "state", state
        );

        return get(path, userId, parameters);
    }


}