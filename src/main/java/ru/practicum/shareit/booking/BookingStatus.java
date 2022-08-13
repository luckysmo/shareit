package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING,//ожидает одобрения
    APPROVED,//подтвержденно владельцем
    REJECTED,//отклонено владельцем
    CANCELED//отменено создателем
}
