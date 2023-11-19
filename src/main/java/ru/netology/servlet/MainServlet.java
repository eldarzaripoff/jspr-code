package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private PostController controller;
  private static final String API_POSTS_PATH = "/api/posts";
  private static final String ANOTHER_API_POSTS_PATH = API_POSTS_PATH + "/\\d+";
  private static final String GET = "GET";
  private static final String POST = "POST";
  private static final String DELETE = "DELETE";

  @Override
  public void init() {
    final var context = new AnnotationConfigApplicationContext(JavaConfig.class);
    final var repository = context.getBean(PostRepository.class);
    final var service = context.getBean(PostService.class);
    this.controller = context.getBean(PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals(GET) && path.equals(API_POSTS_PATH)) {
        controller.all(resp);
        return;
      }
      long l = Long.parseLong(path.substring(path.lastIndexOf("/")));
      if (method.equals(GET) && path.matches(ANOTHER_API_POSTS_PATH)) {
        // easy way
        controller.getById(l, resp);
        return;
      }
      if (method.equals(POST) && path.equals(API_POSTS_PATH)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals(DELETE) && path.matches(ANOTHER_API_POSTS_PATH)) {
        // easy way
        controller.removeById(l, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}

