package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemBookingResponseDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.UpdateItemRequestDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemBookingResponseDTO getItem(long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + id + " не найдена"));
        List<CommentResponseDTO> comments = commentDtos(item.getId());
        ItemBookingResponseDTO itemBookingResponseDTO = ItemMapper.mapToItemBookingDto(item,
                bookingRepository.findLastBookingOfItem(item.getId(), Status.APPROVED, LocalDateTime.now())
                        .stream().findFirst().orElse(null),
                bookingRepository.findNextBookingOfItem(item.getId(), Status.REJECTED, LocalDateTime.now())
                        .stream().findFirst().orElse(null));
        itemBookingResponseDTO.setComments(comments);
        return itemBookingResponseDTO;
    }

    @Override
    public Collection<ItemBookingResponseDTO> getItems(long userId) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        LocalDateTime now = LocalDateTime.now();
        Collection<Item> items = itemRepository.findByOwnerId(userId);

        Collection<ItemBookingResponseDTO> itemBookingResponseDTOS = items.stream()
                .map(item -> ItemMapper.mapToItemBookingDto(item,
                        bookingRepository.findLastBookingOfItem(item.getId(), Status.APPROVED, now)
                                .stream().findFirst().orElse(null),
                        bookingRepository.findNextBookingOfItem(item.getId(), Status.REJECTED, now)
                                .stream().findFirst().orElse(null)))
                .collect(Collectors.toList());
        itemBookingResponseDTOS.forEach(
                item -> item.setComments(commentDtos(item.getId()))
        );
        return itemBookingResponseDTOS;
    }

    @Override
    public ItemResponseDTO createItem(ItemRequestDTO newItem, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
        if (newItem.getName() == null || newItem.getName().isEmpty()) {
            throw new ValidationException("Имя должно быть указано");
        }
        if (newItem.getDescription() == null || newItem.getDescription().isEmpty()) {
            throw new ValidationException("Описание должно быть указано");
        }
        if (newItem.getAvailable() == null) {
            throw new ValidationException("Доступность должна быть указана");
        }
        Item item = ItemMapper.mapToItem(newItem);
        item.setOwner(user);
        long requestId = newItem.getRequestId();
        if (requestId != 0) {
            ItemRequest request = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Запрос с ID: " + requestId + " не найден"));
            item.setRequest(request);
        }
        itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemResponseDTO updateItem(long itemId, UpdateItemRequestDTO itemFields, long userId) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        Item updatedItem = itemRepository.findById(itemId)
                .map(item -> ItemMapper.updateFields(item, itemFields))
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + itemId + " не найдена"));
        return ItemMapper.mapToItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public Collection<ItemResponseDTO> search(long userId, String text) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase().trim();
        return itemRepository.search(lowerText).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public CommentResponseDTO addComment(long authorId, long itemId, Comment comment) {
        String text = comment.getText().trim();
        if (text.isBlank()) {
            throw new ValidationException("Текст отзыва не может быть пустым");
        }
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + authorId + " не найден"));
        Item item = bookingRepository.findByBookerIdOrderByStartDateDesc(authorId).stream()
                .filter(b -> b.getEndDate().isBefore(LocalDateTime.now()))
                .filter(b -> b.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Пользователь не арендовал вещь с ID " + itemId))
                .getItem();
        Comment commentToSave = new Comment();
        commentToSave.setAuthor(author);
        commentToSave.setItem(item);
        commentToSave.setText(text);
        commentToSave.setCreated(LocalDateTime.now());
        commentRepository.save(commentToSave);
        return CommentMapper.mapToCommentDto(commentToSave);
    }

    private boolean isUserExist(long id) {
        return userRepository.findById(id).isPresent();
    }

    private List<CommentResponseDTO> commentDtos(long itemId) {
        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();

    }
}