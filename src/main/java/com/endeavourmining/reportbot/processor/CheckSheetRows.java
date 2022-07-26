/*
 * Copyright (c) 2022 Endeavour Mining
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.endeavourmining.reportbot.processor;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Checks sheet rows.
 *
 * @since 0.2
 */
@SuppressWarnings(
    {
        "PMD.AvoidCatchingGenericException", "PMD.AvoidThrowingNullPointerException",
        "PMD.AvoidCatchingNPE"
    }
)
public final class CheckSheetRows implements SheetValidator {

    /**
     * Row start index.
     */
    private static final int ROW_START_IDX = 7;

    /**
     * Description column index.
     */
    private static final int DESC_COL = 1;

    /**
     * UOM column.
     */
    private static final int UOM_COL = 2;

    /**
     * Report template in Excel.
     */
    private final XSSFWorkbook template;

    /**
     * Ctor.
     * @param template Template
     */
    public CheckSheetRows(final XSSFWorkbook template) {
        this.template = template;
    }

    @Override
    public void validate(final XSSFSheet sheet) throws IOException {
        final XSSFSheet tsheet = this.template.getSheet(sheet.getSheetName());
        final int ercount = CheckSheetRows.countRows(tsheet);
        final int arcount = CheckSheetRows.countRows(sheet);
        if (ercount != arcount) {
            throw new IllegalArgumentException(
                String.format(
                    "Found %s rows where expecting %s. :-( <br>", arcount, ercount
                )
            );
        }
        final Collection<String> errors = new LinkedList<>();
        for (int rowi = CheckSheetRows.ROW_START_IDX; rowi < ercount; rowi = rowi + 1) {
            final XSSFRow erow = tsheet.getRow(rowi);
            final XSSFRow arow = sheet.getRow(rowi);
            final String edesc = erow.getCell(CheckSheetRows.DESC_COL).getStringCellValue();
            final String adesc = arow.getCell(CheckSheetRows.DESC_COL).getStringCellValue();
            if (!edesc.equals(adesc)) {
                errors.add(
                    String.format(
                        "At row %s, <u>Description</u> value is <b>%s</b> instead of <b>%s</b>",
                        rowi + 1, adesc, edesc
                    )
                );
            }
            final String euom = erow.getCell(CheckSheetRows.UOM_COL).getStringCellValue();
            final String auom = arow.getCell(CheckSheetRows.UOM_COL).getStringCellValue();
            if (!euom.equals(auom)) {
                errors.add(
                    String.format(
                        "At row %s, <u>UOM</u> value is <b>%s</b> instead of <b>%s</b>",
                        rowi + 1, euom, auom
                    )
                );
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                String.join("<br>", errors)
            );
        }
    }

    /**
     * Count rows.
     * @param sheet Sheet
     * @return Number of rows
     */
    private static int countRows(final XSSFSheet sheet) {
        int count = 0;
        for (int idx = 0; idx <= sheet.getLastRowNum(); idx = idx + 1) {
            try {
                final XSSFCell cell = sheet.getRow(idx).getCell(CheckSheetRows.DESC_COL);
                if (cell == null) {
                    throw new NullPointerException("Cell null");
                }
                // @checkstyle MagicNumberCheck (1 line)
                if (cell.getStringCellValue().isBlank() && idx > 3) {
                    throw new NullPointerException("Cell blank");
                }
            } catch (final NullPointerException npe) {
                count = idx;
                break;
            }
        }
        return count;
    }
}
