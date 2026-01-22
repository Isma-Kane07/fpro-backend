package com.facturation.service;

import com.facturation.dto.FactureResponseDTO;
import com.facturation.dto.LigneFactureDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

@Service
public class PdfService {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);
    private static final Font TABLE_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Couleurs pour le tableau
    private static final Color HEADER_BG_COLOR = new Color(240, 240, 240); // Gris clair pour l'en-tête
    private static final Color BORDER_COLOR = new Color(200, 200, 200); // Gris moyen pour les bordures

    public byte[] generateFacturePdf(FactureResponseDTO facture) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 25, 25, 25, 25);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            // Filigrane et pied de page
            writer.setPageEvent(new WatermarkAndFooter());

            document.open();

            addHeaderBox(document);
            addFactureMeta(document, facture);
            addTable(document, facture);
            addTotal(document, facture);
            addSignature(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF", e);
        }
    }

    // ================= MÉTHODE FORMATAGE =================
    private String formatAmount(double value) {
        long v = Math.round(value);
        String s = String.valueOf(v);
        StringBuilder sb = new StringBuilder();
        int c = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
            c++;
            if (c % 3 == 0 && i != 0) sb.append('.');
        }
        return sb.reverse().toString();
    }

    // ================= HEADER =================
    private void addHeaderBox(Document document) throws Exception {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{2, 8});

        // Logo
        InputStream logoStream = new ClassPathResource("static/images/md-logo.png").getInputStream();
        Image logo = Image.getInstance(logoStream.readAllBytes());
        logo.scaleToFit(100, 80);
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setRowspan(4);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setPadding(5);
        header.addCell(logoCell);

        // Texte entreprise
        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setPadding(5);

        Paragraph title = new Paragraph("MAHAMADOU DOUCOURE & FRERES", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(title);

        Paragraph p1 = new Paragraph("Moulin - Pièces Détachées - Décortiqueuse Chinoise", HEADER_FONT);
        p1.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(p1);

        Paragraph p2 = new Paragraph("et les pièces - Groupe Electrogène -", HEADER_FONT);
        p2.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(p2);

        Paragraph p3 = new Paragraph("Motopompe -Pompe Immergée - Motoculteur 12 HP", HEADER_FONT);
        p3.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(p3);

        Paragraph p4 = new Paragraph("Tél : 223 96 63 60 00 / 73 23 61 97 / 66 56 63 76 / 94 16 16 10", SMALL_FONT);
        p4.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(p4);

        Paragraph p5 = new Paragraph("Niaréla Dougoutiguila Caré Bamako - Rép du Mali", SMALL_FONT);
        p5.setAlignment(Element.ALIGN_CENTER);
        textCell.addElement(p5);

        header.addCell(textCell);

        // Créer une table externe avec une seule bordure
        PdfPTable outerTable = new PdfPTable(1);
        outerTable.setWidthPercentage(100);

        PdfPCell outerCell = new PdfPCell();
        outerCell.setBorder(Rectangle.BOX);
        outerCell.setPadding(0);
        outerCell.addElement(header);
        outerTable.addCell(outerCell);

        document.add(outerTable);
        document.add(new Paragraph(" "));
    }

    // ================= META =================
    private void addFactureMeta(Document document, FactureResponseDTO facture) throws Exception {
        PdfPTable meta = new PdfPTable(2);
        meta.setWidthPercentage(100);
        meta.setWidths(new float[]{6, 4});

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.addElement(new Paragraph("FACTURE N° : " + facture.getNumeroFacture(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
        left.addElement(new Paragraph("DOIT : " + facture.getNomClient(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        right.setVerticalAlignment(Element.ALIGN_TOP);

        Paragraph dateParagraph = new Paragraph("Bamako, le " + facture.getDateFacturation().format(DATE_FORMATTER),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9));
        dateParagraph.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(dateParagraph);

        meta.addCell(left);
        meta.addCell(right);
        document.add(meta);
        document.add(new Paragraph(" "));
    }

    // ================= TABLE =================
    private void addTable(Document document, FactureResponseDTO facture) throws Exception {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 5f, 2f, 2.5f});
        table.setSpacingBefore(5f);
        table.setSpacingAfter(10f);

        // En-têtes du tableau avec fond gris
        addStyledHeader(table, "QTE");
        addStyledHeader(table, "DESIGNATION");
        addStyledHeader(table, "P.U");
        addStyledHeader(table, "MONTANT");

        // Lignes de données TRANSPARENTES (pas de fond)
        for (LigneFactureDTO l : facture.getLignes()) {
            addTransparentCell(table, l.getQuantite().toString(), Element.ALIGN_CENTER);
            addTransparentCell(table, l.getDesignation(), Element.ALIGN_LEFT);
            addTransparentCell(table, formatAmount(l.getPrixUnitaire()), Element.ALIGN_RIGHT);
            addTransparentCell(table, formatAmount(l.getMontant()) + " FCFA", Element.ALIGN_RIGHT);
        }

        // Lignes vides pour compléter (aussi transparentes)
        int empty = 15 - facture.getLignes().size();
        for (int i = 0; i < empty; i++) {
            addTransparentCell(table, "", Element.ALIGN_CENTER);
            addTransparentCell(table, "", Element.ALIGN_LEFT);
            addTransparentCell(table, "", Element.ALIGN_RIGHT);
            addTransparentCell(table, "", Element.ALIGN_RIGHT);
        }

        document.add(table);
    }

    private void addStyledHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, TABLE_HEADER));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderWidth(1f);
        cell.setBorderColor(BORDER_COLOR);
        cell.setBackgroundColor(HEADER_BG_COLOR); // Fond gris uniquement pour l'en-tête
        cell.setPadding(8);
        cell.setFixedHeight(25);
        table.addCell(cell);
    }

    private void addTransparentCell(PdfPTable table, String text, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(BORDER_COLOR);
        cell.setBackgroundColor(null); // Fond TRANSPARENT
        cell.setPadding(6);
        cell.setFixedHeight(22);
        cell.setNoWrap(false);
        table.addCell(cell);
    }

    // ================= TOTAL =================
    private void addTotal(Document document, FactureResponseDTO facture) throws Exception {
        double total = facture.getLignes().stream().mapToDouble(LigneFactureDTO::getMontant).sum();

        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{7, 3.5f});
        totalTable.setSpacingBefore(10f);

        PdfPCell left = new PdfPCell(new Phrase("TOTAL", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        left.setBorderWidthTop(1.5f);
        left.setBorderWidthRight(0.5f);
        left.setBorderWidthBottom(1.5f);
        left.setBorderWidthLeft(1.5f);
        left.setBorderColor(BORDER_COLOR);
        left.setBackgroundColor(HEADER_BG_COLOR);
        left.setHorizontalAlignment(Element.ALIGN_RIGHT);
        left.setPadding(10);

        PdfPCell right = new PdfPCell(new Phrase(formatAmount(total) + " FCFA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        right.setBorderWidthTop(1.5f);
        right.setBorderWidthRight(1.5f);
        right.setBorderWidthBottom(1.5f);
        right.setBorderWidthLeft(0.5f);
        right.setBorderColor(BORDER_COLOR);
        right.setBackgroundColor(HEADER_BG_COLOR);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        right.setPadding(10);

        totalTable.addCell(left);
        totalTable.addCell(right);

        document.add(totalTable);

        // Montant en lettres
        Paragraph totalLettres = new Paragraph();
        totalLettres.setSpacingBefore(8f);
        totalLettres.add(new Chunk("Arrêté la présente facture à la somme de : ",
                FontFactory.getFont(FontFactory.HELVETICA, 10)));
        totalLettres.add(new Chunk(facture.getTotalEnLettres(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        document.add(totalLettres);
    }

    // ================= SIGNATURE =================
    private void addSignature(Document document) throws Exception {
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        PdfPTable sign = new PdfPTable(2);
        sign.setWidthPercentage(100);
        sign.setWidths(new float[]{1, 1});
        sign.setSpacingBefore(30f);

        PdfPCell left = new PdfPCell(new Phrase("Pour Acquit",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        left.setBorder(Rectangle.NO_BORDER);
        left.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell right = new PdfPCell(new Phrase("Le Fournisseur",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);

        sign.addCell(left);
        sign.addCell(right);

        document.add(sign);
    }

    // ================= WATERMARK + FOOTER =================
    static class WatermarkAndFooter extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                // 1. Filigrane
                InputStream logoStream = new ClassPathResource("static/images/md-logo.png").getInputStream();
                Image logo = Image.getInstance(logoStream.readAllBytes());
                logo.scaleToFit(250, 250);

                float x = (document.getPageSize().getWidth() - logo.getScaledWidth()) / 2;
                float y = (document.getPageSize().getHeight() - logo.getScaledHeight()) / 2;

                logo.setAbsolutePosition(x, y);

                PdfContentByte canvas = writer.getDirectContentUnder();
                PdfGState gs = new PdfGState();
                gs.setFillOpacity(0.08f);
                canvas.setGState(gs);
                canvas.addImage(logo);

                // 2. Pied de page simple sans numéro de page
                PdfContentByte footerCanvas = writer.getDirectContent();
                footerCanvas.saveState();

                float footerY = document.bottom() - 20;
                float centerX = document.getPageSize().getWidth() / 2;
                float pageWidth = document.getPageSize().getWidth();

                // Ligne fine
                float lineWidth = pageWidth * 0.4f;
                float lineStartX = centerX - (lineWidth / 2);
                float lineEndX = centerX + (lineWidth / 2);
                float lineY = footerY + 8;

                footerCanvas.setLineWidth(0.5f);
                footerCanvas.setColorStroke(Color.GRAY);
                footerCanvas.moveTo(lineStartX, lineY);
                footerCanvas.lineTo(lineEndX, lineY);
                footerCanvas.stroke();

                // Texte du pied de page
                footerCanvas.beginText();
                BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.WINANSI, BaseFont.EMBEDDED);
                footerCanvas.setFontAndSize(baseFont, 8);
                footerCanvas.setColorFill(Color.BLACK);

                String footerText = "Merci pour votre confiance !";
                float textWidth = baseFont.getWidthPoint(footerText, 8);
                float textX = centerX - (textWidth / 2);

                footerCanvas.setTextMatrix(textX, footerY);
                footerCanvas.showText(footerText);
                footerCanvas.endText();

                footerCanvas.restoreState();

            } catch (Exception ignored) {}
        }
    }
}