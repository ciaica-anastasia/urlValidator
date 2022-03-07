package urlValidator;

import java.util.List;

import org.junit.*;

public class TestParser {

    //проверка Parser целиком на сложных строках
    @Test
    public void Parser() {
        List<String> links = List.of("http://game.my.srv:19100", "https://стопкоронавирус.рф/?",
                "https://www.javaguides.net/2019/12/java-9-listof-method-create-immutable.html",
                "https://xn--80agodft5c.xn--p1ai/");
        List<List<String>> answers = List.of(List.of("http", "game.my.srv", "19100", "", "", ""),
                List.of("https", "стопкоронавирус.рф", "", "/", "", ""),
                List.of("https", "www.javaguides.net", "", "/2019/12/java-9-listof-method-create-immutable.html", "", ""),
                List.of("https", "xn--80agodft5c.xn--p1ai", "", "/", "", ""));
        UrlParser urlParser = new UrlParser();
        for (int i = 0; i < links.size(); ++i) {
            UrlElement pas = urlParser.parse(links.get(i));
            Assert.assertEquals(pas.scheme, answers.get(i).get(0));
            Assert.assertEquals(pas.host, answers.get(i).get(1));
            Assert.assertEquals(pas.port, answers.get(i).get(2));
            Assert.assertEquals(pas.path, answers.get(i).get(3));
            Assert.assertEquals(pas.query, answers.get(i).get(4));
            Assert.assertEquals(pas.fragment, answers.get(i).get(5));
        }
    }

    //проверка Parser разделения netcol
    @Test
    public void testSplitNetcol() {
        List<String> links = List.of("//www.cwi.nl:80/%7Eguido/Python.html",
                "//github.com/atp-mipt/ljv/tree/master/ljv/src/test/java/org/atpfivt/ljv", "//game.my.srv:19100");

        List<List<String>> answers = List.of(List.of("www.cwi.nl:80", "/%7Eguido/Python.html"),
                List.of("github.com", "/atp-mipt/ljv/tree/master/ljv/src/test/java/org/atpfivt/ljv"),
                List.of("game.my.srv:19100", ""));
        UrlParser urlParser = new UrlParser();
        for (int i = 0; i < answers.size(); ++i) {
            UrlParser.HostPort hostPort = urlParser.splitNetcol(links.get(i));
            Assert.assertEquals(hostPort.hostname, answers.get(i).get(0));
            Assert.assertEquals(hostPort.port, answers.get(i).get(1));
        }
    }

    //проверка Parser разделение host и port
    @Test
    public void testHostInfo() {
        List<String> netlocs = List.of("game.my.srv:1910", "localhost:8888",
                "github.com", "xn--80agodft5c.xn--p1ai");
        List<List<String>> answers = List.of(List.of("game.my.srv", "1910"), List.of("localhost", "8888"),
                List.of("github.com", ""), List.of("xn--80agodft5c.xn--p1ai", ""));
        UrlParser urlParser = new UrlParser();
        for (int i = 0; i < netlocs.size(); ++i) {
            UrlParser.HostPort hostPort = urlParser.hostInfo(netlocs.get(i));
            Assert.assertEquals(hostPort.hostname, answers.get(i).get(0));
            Assert.assertEquals(hostPort.port, answers.get(i).get(1));
        }
    }

    //TODO: testIsValid()
}
