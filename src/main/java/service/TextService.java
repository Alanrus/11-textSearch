package service;

import model.Text;

import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;


public class TextService {
//    private int id;
    private Collection<Text> books = new ArrayList<>();

    public void addFile(String name, Part part, Path path) {

        String id = generateId();
        if (part != null) {
            writeBook(id, part, path);
        }
        create(new Text(id, name));

    }

    public String generateId() {
//        id++;
//        return String.valueOf(id);
//       return String.valueOf(System.currentTimeMillis());
        return UUID.randomUUID().toString();
    }

    public void create(Text book) {
        books.add(book);
    }

    public void writeBook(String id, Part part, Path path) { //записываем файл с таким же ID как имя в базе
        try {
            part.write(path.resolve(id).toString());
            part.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection searchText(String text) { //поиск из списка имени
        Collection<Text> newBook = new ArrayList<>();

        ArrayList<String> result = new ArrayList<>();
        // ArrayList быстро добавляем элементы
        String newId = generateId();
        String resultPath = Paths.get(System.getenv("RESULT_PATH")) + "\\" + newId;
        try {
            FileWriter fw = new FileWriter(resultPath, true);
            // FileWriter наследуется от класса OutputStreamWriter. Класс используется для записи потоков символов.
            for (Text book : books) {
                String id = book.getId();
                String path = Paths.get(System.getenv("UPLOAD_PATH")) + "\\" + id;
                if (new File(path).exists()) {
                    BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                    //  BufferedReader читает из Reader’а данные большими кусками и хранит их у себя внутри в буфере.
                    result.add("[" + book.getName() + ".txt]: ");

                    String line;
                    while ((line = bf.readLine()) != null) {
                        if (line.toLowerCase().contains(text.toLowerCase())) {
                            result.add(line);


                        }
                    }
                    result.add("\n");
                }
            }
            for (String t : result) {
                fw.write(t);
                // Запись содержимого в файл
                fw.append(System.getProperty("line.separator")); // перенос строки в результативном файле
                fw.flush();
                //чтобы данные легли в файл, сразу же после операции записи ставь flush().
                //void flush() очищает любые выходные буферы, завершая операцию вывода.
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(newId + " ID");
        newBook.add(new Text(newId, text));
        return newBook;
    }


    public Collection<Text> getBooks() {
        return books;
    }
}
