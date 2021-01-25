package com.example.famreen.application.interfaces

interface ItemHelper {
    /**
     * Функция должна вызыватся когда элемент будет свяйпнут
     * Параметром следует передать позицию свайпнутого элемента
     * **/
    fun onItemDismiss(position: Int)
}