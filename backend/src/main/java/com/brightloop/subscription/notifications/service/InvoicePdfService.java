package com.brightloop.subscription.notifications.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.entity.InvoiceLine;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.common.util.MoneyUtils;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
public class InvoicePdfService {

    private final InvoiceRepository invoiceRepository;

    public InvoicePdfService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Renders an invoice as a PDF byte array matching the invoice layout.
     *
     * @param invoiceId ID of the invoice to render
     * @return PDF content as byte array
     */
    @Transactional(readOnly = true)
    public byte[] generateInvoicePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + invoiceId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Setup fonts
            BaseFont baseFont;
            try {
                String winFont = "C:/Windows/Fonts/arial.ttf";
                if (new File(winFont).exists()) {
                    baseFont = BaseFont.createFont(winFont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                } else {
                    baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                }
            } catch (Exception e) {
                baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }

            Color brandColor = new Color(30, 58, 138); // #1e3a8a
            Color darkText = new Color(31, 41, 55);    // #1f2937
            Color grayText = new Color(107, 114, 128); // #6b7280
            Color borderColor = new Color(229, 231, 235); // #e5e7eb
            Color zebraColor = new Color(249, 250, 251);  // #f9fafb
            Color totalBgColor = new Color(239, 246, 255); // #eff6ff
            Color primaryBlue = new Color(37, 99, 235);   // #2563eb

            Font brandFont = new Font(baseFont, 22, Font.BOLD, brandColor);
            Font invoiceTitleFont = new Font(baseFont, 18, Font.BOLD, darkText);
            Font subHeaderFont = new Font(baseFont, 10, Font.NORMAL, grayText);
            Font sectionHeadingFont = new Font(baseFont, 9, Font.BOLD, grayText);
            Font normalBold = new Font(baseFont, 10, Font.BOLD, darkText);
            Font normalText = new Font(baseFont, 10, Font.NORMAL, darkText);
            Font smallText = new Font(baseFont, 9, Font.NORMAL, darkText);
            Font tableHeaderFont = new Font(baseFont, 9, Font.BOLD, darkText);
            Font totalFont = new Font(baseFont, 12, Font.BOLD, brandColor);
            Font footerFont = new Font(baseFont, 8, Font.NORMAL, grayText);

            // 1. Header (Brand left, INVOICE right)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{50, 50});
            headerTable.setSpacingAfter(18f);

            PdfPCell leftHeader = new PdfPCell(new Phrase("Brightloop", brandFont));
            leftHeader.setBorder(Rectangle.BOTTOM);
            leftHeader.setBorderColor(primaryBlue);
            leftHeader.setBorderWidth(2f);
            leftHeader.setPaddingBottom(10f);
            headerTable.addCell(leftHeader);

            Paragraph rightPara = new Paragraph();
            rightPara.setAlignment(Element.ALIGN_RIGHT);
            rightPara.add(new Chunk("INVOICE\n", invoiceTitleFont));
            rightPara.add(new Chunk(invoice.getNumber(), subHeaderFont));

            PdfPCell rightHeader = new PdfPCell(rightPara);
            rightHeader.setHorizontalAlignment(Element.ALIGN_RIGHT);
            rightHeader.setBorder(Rectangle.BOTTOM);
            rightHeader.setBorderColor(primaryBlue);
            rightHeader.setBorderWidth(2f);
            rightHeader.setPaddingBottom(10f);
            headerTable.addCell(rightHeader);

            document.add(headerTable);

            // 2. Two-column block: Bill to & Invoice details
            Customer customer = invoice.getCustomer();
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.setWidths(new float[]{50, 50});
            detailsTable.setSpacingAfter(20f);

            // Left column: Bill To
            Paragraph billTo = new Paragraph();
            billTo.add(new Chunk("BILL TO\n", sectionHeadingFont));
            String customerName = customer != null ? customer.getContactName() : "Valued Customer";
            billTo.add(new Chunk(customerName + "\n", normalBold));
            if (customer != null && customer.getCompanyName() != null && !customer.getCompanyName().isBlank()) {
                billTo.add(new Chunk(customer.getCompanyName() + "\n", normalText));
            }
            if (customer != null && customer.getBillingAddress() != null && !customer.getBillingAddress().isBlank()) {
                billTo.add(new Chunk(customer.getBillingAddress() + "\n", smallText));
            }
            PdfPCell billToCell = new PdfPCell(billTo);
            billToCell.setBorder(Rectangle.NO_BORDER);
            detailsTable.addCell(billToCell);

            // Right column: Invoice details
            Paragraph invDetails = new Paragraph();
            invDetails.add(new Chunk("INVOICE DETAILS\n", sectionHeadingFont));
            invDetails.add(new Chunk("Issue Date: ", normalBold));
            invDetails.add(new Chunk(invoice.getIssueDate().toString() + "\n", normalText));
            invDetails.add(new Chunk("Due Date: ", normalBold));
            invDetails.add(new Chunk(invoice.getDueDate().toString() + "\n", normalText));
            invDetails.add(new Chunk("Subscription: ", normalBold));
            invDetails.add(new Chunk(invoice.getSubscription().getNumber() + "\n", normalText));
            invDetails.add(new Chunk("Payment Status: ", normalBold));
            invDetails.add(new Chunk(invoice.getStatus() + "\n", normalText));

            PdfPCell invDetailsCell = new PdfPCell(invDetails);
            invDetailsCell.setBorder(Rectangle.NO_BORDER);
            detailsTable.addCell(invDetailsCell);

            document.add(detailsTable);

            // 3. Line items table (Description, Qty, Unit price, Tax, Amount)
            PdfPTable itemsTable = new PdfPTable(5);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{44, 10, 16, 14, 16});
            itemsTable.setSpacingAfter(15f);

            // Header row
            String[] headers = {"Description", "Qty", "Unit price", "Tax", "Amount"};
            int[] alignments = {Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_RIGHT, Element.ALIGN_RIGHT, Element.ALIGN_RIGHT};

            for (int i = 0; i < headers.length; i++) {
                PdfPCell th = new PdfPCell(new Phrase(headers[i], tableHeaderFont));
                th.setBackgroundColor(new Color(243, 244, 246));
                th.setBorderColor(borderColor);
                th.setPadding(8f);
                th.setHorizontalAlignment(alignments[i]);
                itemsTable.addCell(th);
            }

            // Data rows
            int rowIndex = 0;
            if (invoice.getLines() != null && !invoice.getLines().isEmpty()) {
                for (InvoiceLine line : invoice.getLines()) {
                    Color rowBg = (rowIndex % 2 == 1) ? zebraColor : Color.WHITE;

                    // Description
                    PdfPCell c1 = new PdfPCell(new Phrase(line.getDescription(), normalText));
                    c1.setBackgroundColor(rowBg);
                    c1.setBorderColor(borderColor);
                    c1.setPadding(7f);
                    itemsTable.addCell(c1);

                    // Qty
                    PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(line.getQuantity()), normalText));
                    c2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c2.setBackgroundColor(rowBg);
                    c2.setBorderColor(borderColor);
                    c2.setPadding(7f);
                    itemsTable.addCell(c2);

                    // Unit price (formatted in Java)
                    PdfPCell c3 = new PdfPCell(new Phrase(MoneyUtils.formatPaiseToRupees(line.getUnitPricePaise()), normalText));
                    c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    c3.setBackgroundColor(rowBg);
                    c3.setBorderColor(borderColor);
                    c3.setPadding(7f);
                    itemsTable.addCell(c3);

                    // Tax (formatted in Java)
                    PdfPCell c4 = new PdfPCell(new Phrase(MoneyUtils.formatPaiseToRupees(line.getTaxPaise()), normalText));
                    c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    c4.setBackgroundColor(rowBg);
                    c4.setBorderColor(borderColor);
                    c4.setPadding(7f);
                    itemsTable.addCell(c4);

                    // Amount (formatted in Java)
                    PdfPCell c5 = new PdfPCell(new Phrase(MoneyUtils.formatPaiseToRupees(line.getLineTotalPaise()), normalText));
                    c5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    c5.setBackgroundColor(rowBg);
                    c5.setBorderColor(borderColor);
                    c5.setPadding(7f);
                    itemsTable.addCell(c5);

                    rowIndex++;
                }
            } else {
                // If invoice lines empty, show single summary line
                PdfPCell c1 = new PdfPCell(new Phrase("Subscription Fee - " + invoice.getSubscription().getNumber(), normalText));
                c1.setBorderColor(borderColor);
                c1.setPadding(7f);
                itemsTable.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Phrase("1", normalText));
                c2.setHorizontalAlignment(Element.ALIGN_CENTER);
                c2.setBorderColor(borderColor);
                c2.setPadding(7f);
                itemsTable.addCell(c2);

                PdfPCell c3 = new PdfPCell(new Phrase(MoneyUtils.formatPaiseToRupees(invoice.getSubtotalPaise()), normalText));
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c3.setBorderColor(borderColor);
                c3.setPadding(7f);
                itemsTable.addCell(c3);

                PdfPCell c4 = new PdfPCell(new Phrase(MoneyUtils.formatPaiseToRupees(invoice.getTaxPaise()), normalText));
                c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c4.setBorderColor(borderColor);
                c4.setPadding(7f);
                itemsTable.addCell(c4);

                PdfPCell c5 = new PdfPCell(new Phrase(MoneyUtils.formatPaiseToRupees(invoice.getTotalPaise()), normalText));
                c5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c5.setBorderColor(borderColor);
                c5.setPadding(7f);
                itemsTable.addCell(c5);
            }

            document.add(itemsTable);

            // 4. Totals block right-aligned: Subtotal, Discount, Tax, Total
            PdfPTable outerTotalsTable = new PdfPTable(2);
            outerTotalsTable.setWidthPercentage(100);
            outerTotalsTable.setWidths(new float[]{55, 45});
            outerTotalsTable.setSpacingAfter(25f);

            PdfPCell emptyLeft = new PdfPCell();
            emptyLeft.setBorder(Rectangle.NO_BORDER);
            outerTotalsTable.addCell(emptyLeft);

            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(100);
            totalsTable.setWidths(new float[]{50, 50});

            addTotalRow(totalsTable, "Subtotal:", MoneyUtils.formatPaiseToRupees(invoice.getSubtotalPaise()), normalText, false, null);
            addTotalRow(totalsTable, "Discount:", MoneyUtils.formatPaiseToRupees(invoice.getDiscountPaise()), normalText, false, null);
            addTotalRow(totalsTable, "Tax:", MoneyUtils.formatPaiseToRupees(invoice.getTaxPaise()), normalText, false, null);
            addTotalRow(totalsTable, "Total:", MoneyUtils.formatPaiseToRupees(invoice.getTotalPaise()), totalFont, true, totalBgColor);

            PdfPCell totalsCell = new PdfPCell(totalsTable);
            totalsCell.setBorder(Rectangle.NO_BORDER);
            outerTotalsTable.addCell(totalsCell);

            document.add(outerTotalsTable);

            // 5. Footer
            Paragraph footer = new Paragraph("Payment is due by the due date shown above. Questions? Contact support@brightloop.test.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15f);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate PDF for invoice id: " + invoiceId, e);
        }

        return baos.toByteArray();
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font font, boolean isTotal, Color bg) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, font));
        lCell.setPadding(5f);
        if (isTotal) {
            lCell.setBorder(Rectangle.TOP);
            lCell.setBorderColor(new Color(37, 99, 235));
            lCell.setBorderWidth(1.5f);
            lCell.setBackgroundColor(bg);
        } else {
            lCell.setBorder(Rectangle.NO_BORDER);
        }
        table.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(value, font));
        vCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        vCell.setPadding(5f);
        if (isTotal) {
            vCell.setBorder(Rectangle.TOP);
            vCell.setBorderColor(new Color(37, 99, 235));
            vCell.setBorderWidth(1.5f);
            vCell.setBackgroundColor(bg);
        } else {
            vCell.setBorder(Rectangle.NO_BORDER);
        }
        table.addCell(vCell);
    }
}
