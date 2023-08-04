package com.medinet.integration.rest;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public interface WiremockTestSupport {
    default void stubForInvoice(WireMockServer wireMockServer, String htmlContent, byte[] pdfBytes) {
        wireMockServer.stubFor(post(urlEqualTo("/pdf"))
                .withRequestBody(equalTo(htmlContent))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(pdfBytes)));
    }

    default String testHtml() {
        return "<!doctype html>" +
                "<html>" +
                "" +
                "<head>" +
                "    <meta charset=utf-8>" +
                "" +
                "    <style>" +
                "        .invoice-box {" +
                "            max-width: 800px;" +
                "            padding: 30px;" +
                "            border: 1px solid #eee;" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, .15);" +
                "            font-size: 16px;" +
                "            line-height: 24px;" +
                "            font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;" +
                "            color: #555;" +
                "        }" +
                "" +
                "        .invoice-box table {" +
                "            width: 100%;" +
                "            line-height: inherit;" +
                "            text-align: left;" +
                "        }" +
                "" +
                "        .invoice-box table td {" +
                "            padding: 5px;" +
                "            vertical-align: top;" +
                "        }" +
                "" +
                "        .invoice-box table tr td:nth-child(2) {" +
                "            text-align: right;" +
                "        }" +
                "" +
                "        .invoice-box table tr.top table td {" +
                "            padding-bottom: 20px;" +
                "        }" +
                "" +
                "        .invoice-box table tr.top table td.title {" +
                "            font-size: 45px;" +
                "            line-height: 45px;" +
                "            color: #333;" +
                "        }" +
                "" +
                "        .invoice-box table tr.information table td {" +
                "            padding-bottom: 40px;" +
                "        }" +
                "" +
                "        .invoice-box table tr.heading td {" +
                "            background: #eee;" +
                "            border-bottom: 1px solid #ddd;" +
                "            font-weight: bold;" +
                "        }" +
                "" +
                "        .invoice-box table tr.details td {" +
                "            padding-bottom: 20px;" +
                "        }" +
                "" +
                "        .invoice-box table tr.item td {" +
                "            border-bottom: 1px solid #eee;" +
                "        }" +
                "" +
                "        .invoice-box table tr.item.last td {" +
                "            border-bottom: none;" +
                "        }" +
                "" +
                "        .invoice-box table tr.total td:nth-child(2) {" +
                "            border-top: 2px solid #eee;" +
                "            font-weight: bold;" +
                "        }" +
                "" +
                "        @media only screen and (max-width: 600px) {" +
                "            .invoice-box table tr.top table td {" +
                "                width: 100%;" +
                "                display: block;" +
                "                text-align: center;" +
                "            }" +
                "" +
                "            .invoice-box table tr.information table td {" +
                "                width: 100%;" +
                "                display: block;" +
                "                text-align: center;" +
                "            }" +
                "        }" +
                "" +
                "        /** RTL **/" +
                "        .rtl {" +
                "            direction: rtl;" +
                "            font-family: Tahoma, 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;" +
                "        }" +
                "" +
                "        .rtl table {" +
                "            text-align: right;" +
                "        }" +
                "" +
                "        .rtl table tr td:nth-child(2) {" +
                "            text-align: left;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "" +
                "<body>" +
                "<div class=invoice-box>" +
                "    <table cellpadding=0 cellspacing=0>" +
                "        <tr class=top>" +
                "            <td colspan=2>" +
                "                <table>" +
                "                    <tr>" +
                "                        <td class=title>" +
                "                            <h6 style=color:blue>medinet</h6>" +
                "                        </td>" +
                "" +
                "                        <td>" +
                "                            Faktura:  3abcc6b0-8c55-4ee6-a0ea-4b1cffe881da <br>" +
                "                            Data wizyty:  2023-08-04 <br>" +
                "                            Godzina wizyty:  08:00 +<br>" +
                "                            Adres placówki:  Wrocław adminowa 32 " +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +
                "" +
                "        <tr class=heading>" +
                "            <td>" +
                "               Tytuł faktury" +
                "            </td>" +
                "" +
                "            <td>" +
                "             wizyta w przychodni Medinet" +
                "            </td>" +
                "        </tr>" +
                "" +
                "" +
                "        <tr class=heading>" +
                "            <td>" +
                "                Usługa" +
                "            </td>" +
                "" +
                "            <td>" +
                "                wizyta u specjalisty - Kardiolog " +
                "            </td>" +
                "        </tr>" +
                "        <tr class=item>" +
                "            <td>" +
                "                Lekarz" +
                "            </td>" +
                "" +
                "            <td>" +
                "                Krystian Wieczorek" +
                "            </td>" +
                "        </tr>" +
                "        <tr class=item>" +
                "            <td>" +
                "                Pacjent" +
                "            </td>" +
                "" +
                "            <td>" +
                "                 Admin Admin" +
                "            </td>" +
                "        </tr>" +
                "        <tr class=item>" +
                "            <td>" +
                "                Czas trwania wizyty" +
                "            </td>" +
                "" +
                "            <td>" +
                "               60 minut" +
                "            </td>" +
                "        </tr>" +
                "" +
                "            <td>" +
                "                Metoda płatności" +
                "            </td>" +
                "" +
                "            <td>" +
                "                Przelew" +
                "            </td>" +
                "" +
                "        <tr class=total>" +
                "            <td></td>" +
                "" +
                "            <td>" +
                "                Koszt:  291.00 zł" +
                "            </td>" +
                "        </tr>" +
                "        <tr class=item>" +
                "            <td>" +
                "                Data wystawienia faktury" +
                "            </td>" +
                "" +
                "            <td>" +
                "                 2023:08:04 13:20" +
                "            </td>" +
                "        </tr>" +
                "        <tr class=item>" +
                "" +
                "            <td>" +
                "                <p style=font-weight:bold>Informacja od lekarza</p>" +
                "                <span> Zalecam panadol</span>" +
                "            </td>" +
                "" +
                "        </tr>" +
                "    </table>" +
                "</div>" +
                "</body>" +
                "" +
                "</html>";
    }
}
