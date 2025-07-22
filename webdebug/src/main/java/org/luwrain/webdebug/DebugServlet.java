// NDA, Copyright Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.webdebug;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import jakarta.servlet.annotation.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.apache.velocity.*;

import static java.util.stream.Collectors.*;
import static java.nio.file.Files.*;
import static org.luwrain.webdebug.TextUtils.*;

@WebServlet("/*")
public class DebugServlet extends HttpServlet
{
    static final Path FILES_DIR = Paths.get("/files");

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	if (req.getRequestURI().startsWith("/css/"))
	{
	final var text = readTextFile(FILES_DIR.resolve(req.getRequestURI().substring(5)));
	resp.setContentType("text/css");
        resp.setStatus(HttpServletResponse.SC_OK);
	resp.getWriter().println(text.stream().collect(joining("\n")));
	return;
    }

    switch(req.getRequestURI())
    {
    case "/":
	resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
	resp.getWriter().println(main());
	return;
    case "/login":
	resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
	resp.getWriter().println(login(false));
	return;
    case "/login-values":
	resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
	resp.getWriter().println(login(true));
	return;
	    case "/capture":
	resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
	resp.getWriter().println(capture());
	return;
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
}

    String main() throws IOException
    {
	final var c = new VelocityContext();
	c.put("items", List.of(
			       new MainPageItem("Презентация", "ТомскОтрыв 2025", "3 часа назад"),
			       new MainPageItem("ВКР", "Основы астрономии в контексте развития астрологии", "7 часов назад"),
			       new MainPageItem("Статья", "Роль безумия в разработке ядра Linux", "2 недели назад")
			       ));
	return render(c, "main.vm");
	    }

        String login(boolean putValues) throws IOException
    {
	final var c = new VelocityContext();
	c.put("login", putValues?"Chupakabra":"");
		c.put("passwd", putValues?"****":"");
				c.put("message", putValues?"Чупакабры не существует. Регистрация отклонена.":"");
	return render(c, "login.vm");
	    }

        String capture() throws IOException
    {
	final var c = new VelocityContext();
	c.put("audio", "https://luwrain.social/capture/audio.mp3");
		c.put("image", "https://luwrain.social/capture/image.png");
				c.put("attemptsLeft", "5");
	return render(c, "capture.vm");
	    }


    String render(VelocityContext context, String templateName) throws IOException
    {
	final var eng = new Engine(loadTemplates());
			final var templ = eng.engine.getTemplate(templateName);
	final StringWriter w = new StringWriter();
	templ.merge( context, w );
	return w.toString();
	    }

    Map<String, String> loadTemplates() throws IOException
    {
	return walk(FILES_DIR)
	.filter(f -> f.getFileName().toString().endsWith(".vm"))
	.collect(toMap(f -> f.getFileName().toString(), f -> readTextFile(f).stream().collect(joining("\n"))));
    }
}
