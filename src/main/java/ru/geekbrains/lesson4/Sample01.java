package ru.geekbrains.lesson4;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class Sample01 {

    public static void main(String[] args) {

    }

}

class ReaderService implements Readable{

    private ArrayList<String> lines;


    public Collection<String>  readTextFile(File file){

        // ПРЕДУСЛОВИЕ
        if(!file.exists())
                throw new RuntimeException("Файл не найден.");
        if (file.length() > 5000){
            throw new RuntimeException("Размер файла более 5мб. Чтение файла запрещено.");
        }

        // Основные инструкции
        //....
        try {
            lines = new ArrayList<>();

            lines.clear();

            // ИНВАРИАНТ
            validateResult(lines);

        }
        catch (Exception e){

        }

        //....


        //ПОСТУСЛОВИЕ
        if (lines == null)
            throw new RuntimeException("Ошибка чтения файла.");

        return  lines;

    }

    private void validateResult(Collection<String> lines){
        if (lines.size() == 0){
            throw new RuntimeException("Файл пуст.");
        }

    }

}

interface Readable{

    /**
     * Позволяет считывать строки из текстового файла
     * @param file файл
     * @throws RuntimeException исключение при работе с файлом
     * @return коллекция строк
     */
    Collection<String>  readTextFile(File file);

}


