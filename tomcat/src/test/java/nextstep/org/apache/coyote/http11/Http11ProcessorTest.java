package nextstep.org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final StubSocket socket = new StubSocket();
        final Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 12 ",
                "Content-Type: text/html;charset=utf-8 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expected = "HTTP/1.1 200 OK \r\n" +
                       "Content-Length: 5518 \r\n" +
                       "Content-Type: text/html;charset=utf-8 \r\n" +
                       "\r\n" +
                       new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
