package frontend;

import templater.PageGenerator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Wequb
 *  and Rolandius on 02:14 22.02.2014
 */

public class Frontend extends HttpServlet {

    private final Map<String, String> users = new HashMap<>();
    private HttpSession session;
    private AtomicLong userIdGenerator = new AtomicLong();

    public Frontend() {
        users.put("java", "coffee");
        users.put("coffee", "java");
    }

    public String getTime() {
        Date date = new Date();
        date.getTime();
        DateFormat formatter = new SimpleDateFormat("HH.mm.ss");
        return formatter.format(date);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        session = request.getSession();
        if (session.getAttribute("login") == null || session.getAttribute("pass") == null) {
            response.sendRedirect("/");
            return;
        }

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> pageVariables = new HashMap<>();
        if (request.getPathInfo().equals("/timer")) {
            pageVariables.put("refreshPeriod", "1000");
            pageVariables.put("serverTime", getTime());
            pageVariables.put("sessionId", session.getAttribute("userId"));
            response.getWriter().println(PageGenerator.getPage("timer.html", pageVariables));
            return;
        }
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        session = request.getSession();
        if (request.getParameter("login").equals(session.getAttribute("login")) &&
                request.getParameter("pass").equals(session.getAttribute("pass")) ) {
            response.sendRedirect("/timer");
            return;
        }

        if (users.containsKey(request.getParameter("login")) &&
                request.getParameter("pass").equals(users.get(request.getParameter("login")))) {
            session.invalidate();
            session = request.getSession();
            session.setAttribute("login", request.getParameter("login"));
            session.setAttribute("pass", request.getParameter("pass"));
            session.setAttribute("userId", userIdGenerator.getAndIncrement());
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);

            if (request.getPathInfo().equals("/timer")) {
                response.sendRedirect("/timer");
                return;
            }
        }
        else {
            response.sendRedirect("/");
            return;
        }

    }
}
