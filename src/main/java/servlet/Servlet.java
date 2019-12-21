package servlet;

import model.Text;
import service.TextService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class Servlet extends HttpServlet {

    private TextService textService;
    private Path uploadPath;
    private Path resultPath;
    private Collection<Text> texts;


    @Override
    public void init() {
        textService = new TextService();
        uploadPath = Paths.get(System.getenv("UPLOAD_PATH"));
        // Paths — это совсем простой класс с единственным статическим методом get(). Нужен, из переданной строки или URI получить объект типа Path.
        // Path — это переработанный аналог класса File
        // Класс File - управлять файлами на диске компьютера.
        // getenv получает переменную окружения
        resultPath = Paths.get(System.getenv("RESULT_PATH"));
        texts = new ArrayList<>();

        if (Files.notExists(uploadPath)) {
            try {
                Files.createDirectory(uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (Files.notExists(resultPath)) {
            try {
                Files.createDirectory(resultPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
// HttpServletRequest req - получить отправленные сервлету данные, то есть параметры запроса. Для этого в классе HttpServletRequest определены два метода:
//getParameter(String param) возвращает значение определенного параметра, название которого передается в метод. Если указанного параметра в запросе нет, то возвращается null.
//getParameterValues(String param) возвращает массив значений, который представляет определенный параметр. Если указанного параметра в запросе нет, то возвращается null.
        String url = req.getRequestURI().substring(req.getContextPath().length()); //получаем URL запроса
// getRequestURI() возвращает строку, содержащую часть URL от имени протокола до строки запроса, легко модифицировать url.
        // substring возвращает часть строки. Существует два варианта этого метода.
        //Первый возвращает подстроку, заданную начальным и конечным номерами символов.
        // Во втором варианте методы мы указываем только начальный индекс подстроки, и он возвращает подстроку от этого индекса до конца строки.
               req.setCharacterEncoding("UTF-8");
//               resp.setCharacterEncoding("UTF-8");
               //Для определения кодировки данных ответа необходимо использовать метод setCharacterEncoding
        req.setAttribute("books", textService.getBooks());
        // Наиболее распространенный способ передачи данных из сервлета в jsp представляют атрибуты запроса.
        // у объекта HttpServletRequest, который передается в сервлет, вызывается метод setAttribute().
        // Этот метод устанавливает атрибут, который можно получить в jsp.

// Метод GET предназначен для получения файла по запросу (URL). Запрос файла предполагает, что ничего кроме самого запроса на сервер не пересылается.
//Метод PUT предназначен для добавления файла на сервер. При этом ожидается, что путем файла будет путь, указанный в URL. Тело запроса должно содержать такой файл.
//Метод POST предназначен для обновления файла на сервере. И в запросе, и в ответе пересылаются данные/файлы.
//Метод DELETE предназначен для удаления файла по его URL.

        if (req.getMethod().equals("POST")) { // метод - сохраняем файл
            // getMethod() и getMethods() возвращают только открытые методы
            if (req.getParameter("action").equals("save")) {
                Part file = req.getPart("file");
                // Part интерфейс - Gets the content of this part as an <tt>InputStream</tt>
                String name = Paths.get(file.getSubmittedFileName()).getFileName().toString();//получаем имя из файла
                int length = name.length();
                int lengthTxt = ".txt".length();
                name = name.substring(0, length - lengthTxt); //вырезаем .txt
                textService.addFile(name, file, uploadPath); //добавляем в список файл
                resp.sendRedirect(req.getRequestURI());
                //Метод forward() интерфейса RequestDispatcher используется для передачи запроса другому ресурсу внутри сервлета.
                // То есть действие выполняется в один шаг. Метод sendRedirect() интерфейса ServletResponse является двухшаговым.
                // В этом методе WEB-приложение возвращает ответ клиенту со статусом кода 302 (redirect) и с ссылкой для отправки запроса.
                // Браузер отправляет полностью новый запрос по полученной ссылке. То есть, forward() обрабатывается внутри контейнера,
                // а sendRedirect() обрабатывается в браузере.
                //Для организации перехода внутри одного и того же приложения необходимо использовать forward(),
                // т.к. данный метод реагирует быстрее, чем sendRedirect(), использующий дополнительный сетевой ресурс.
                //При использовании метода forward() адрес URL в строке остается прежним, т.к. браузер не знает о фактически обрабатываемом ресурсе. В методе sendRedirect() адрес URL изменяется на пробрасываемый ресурс.
                return;
            }


            if (req.getParameter("action").equals("search")) { //поиск по названию


                String searchName = req.getParameter("search"); // поисковый запрос
                texts = textService.searchText(searchName); // отдал поисковый запрос
                req.setAttribute("catalog", texts);
                req.getRequestDispatcher("/WEB-INF/searchPage.jsp").forward(req, resp);
                return;

            }

            if (req.getParameter("action").equals("return")) {
                req.getRequestDispatcher("/WEB-INF/mainPage.jsp").forward(req, resp);
                return;
            }
        }

        if (url.equals("/search")) { // для запроса /search
            if (req.getMethod().equals("GET")) {
//                System.out.println(url);
                req.setAttribute("catalog", texts);
                req.getRequestDispatcher("/WEB-INF/searchPage.jsp").forward(req, resp);
                return;
            }
        }

        if (url.startsWith("/text/")) {
            String id = url.substring("/text/".length());
            System.out.println(id + " file");
            Path text = resultPath.resolve(id);
            System.out.println(text);
            if (Files.exists(text)) {
                Files.copy(text, resp.getOutputStream());
                return;
            }

            try {
                Files.copy(Paths.get(getServletContext().getResource("/WEB-INF/404.jpg").toURI()), resp.getOutputStream());
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }


        req.getRequestDispatcher("/WEB-INF/mainPage.jsp").forward(req, resp);


    }
}
