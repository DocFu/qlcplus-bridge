package de.plasmawolke.qlcplusbridge.qlc;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VirtualConsoleClient {

    private static final Logger logger = LoggerFactory.getLogger(VirtualConsoleClient.class);


    private HttpClient httpClient;
    private String url;


    private List<VirtualConsoleButton> buttons = new ArrayList<>();

    public VirtualConsoleClient(String url) {
        this.url = url;
        httpClient = new HttpClient();
        httpClient.setFollowRedirects(false);

    }


    private Optional<String> sendRequestAndGetHtml() throws Exception {
        httpClient.start();
        Request request;
        request = httpClient.newRequest(url).method(HttpMethod.GET)
                .agent("QLC+ Bridge - Virtual Console Client");
        ContentResponse response = request.send();
        String html = response.getContentAsString();
        httpClient.stop();
        return Optional.ofNullable(html);
    }

    public List<VirtualConsoleButton> collectButtons() throws NoButtonsFoundException, VirtualConsoleUnavailableException {

        Optional<String> optionalHtml = null;


        try {
            optionalHtml = sendRequestAndGetHtml();
            if (optionalHtml.isEmpty()) {
                throw new VirtualConsoleUnavailableException("HTML is empty.");
            }
        } catch (Exception e) {
            throw new VirtualConsoleUnavailableException("Could not connect to virtual console on '" + url + "'.", e);
        }


        Document doc = Jsoup.parse(optionalHtml.get());
        Elements links = doc.select("a.vcbutton");


        List<VirtualConsoleButton> buttons = new ArrayList<>();

        for (Element element : links) {


            boolean collect = isMarkedForCollection(element);

            if (collect) {

                Integer id = createId(element);
                String name = createName(element);
                boolean on = isOn(element);

                VirtualConsoleButton virtualConsoleButton = new VirtualConsoleButton(id, name, on);
                buttons.add(virtualConsoleButton);
            }


        }

        if (buttons.isEmpty()) {
            throw new NoButtonsFoundException();
        }

        return buttons;

    }

    /**
     * Creates an id.
     *
     * @param element - the virtual console html which represents a button
     * @return the Id
     */
    private Integer createId(Element element) {
        String htmlId = element.attr("id");
        if (StringUtils.isNumeric(htmlId)) {
            Integer originId = Integer.parseInt(htmlId);
            if (originId > 1) {
                return originId;
            }
        }
        logger.warn("Unexpected html button id for element {id='" + htmlId + "'}. Will use hash code instead: " + htmlId.hashCode());
        return htmlId.hashCode();
    }

    /**
     * Creates a name.
     *
     * @param element - the virtual console html which represents a button
     * @return the name
     */
    private String createName(Element element) {
        String name = element.text();
        if (StringUtils.isNotBlank(name)) {
            name = name.trim();
            if (name.length() > 10) {
                logger.info("Label of the button '" + name + "' seems to be long. You might consider to rename it and choose a shorter name.");
            }
        } else {
            name = "Button " + createId(element);
        }
        return name;
    }

    /**
     * Checks if the button style has set a blue font color.
     *
     * @param element - the virtual console html which represents a button
     * @return the evaluation result
     */
    private boolean isMarkedForCollection(Element element) {
        String style = element.attr("style");
        boolean bool = style.contains("color: #0000ff;") || style.contains("color: #0000FF;") || style.contains("color: blue;");
        return bool;
    }

    /**
     * If button has a green border it is considered on, else off.
     *
     * @param element - the virtual console html which represents a button
     * @return the evaluation result
     */
    private boolean isOn(Element element) {
        String style = element.attr("style");
        boolean bool = style.contains("#00E600") || style.contains("#00e600");
        return bool;
    }


    public static void main(String[] args) throws Exception {

        VirtualConsoleClient vcc = new VirtualConsoleClient("http://localhost:9999/");
        List<VirtualConsoleButton> buttons = vcc.collectButtons();
        logger.info("Collected buttons: " + buttons);

    }


}