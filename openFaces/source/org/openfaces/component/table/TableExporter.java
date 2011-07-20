/*
 * OpenFaces - JSF Component Library 2.0
 * Copyright (C) 2007-2011, TeamDev Ltd.
 * licensing@openfaces.org
 * Unless agreed in writing the contents of this file are subject to
 * the GNU Lesser General Public License Version 2.1 (the "LGPL" License).
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * Please visit http://openfaces.org/licensing/ for more details.
 */

package org.openfaces.component.table;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This is a base class for all table exporters. The actual file format is defined by sub-classes of this class.
 *
 * @author Dmitry Pikhulya
 */
public abstract class TableExporter {
    /**
     * This method can be invoked on the Invoke Application phase (that is when application's actions are executed), to
     * export the specified data according to this exporter's logic, and send the appropriate response to the client
     * agent.
     * @param context the current FacesContext instance
     * @param tableData data that should be exported
     * @param fileName name for the file that is sent to the client agent
     */
    public void sendResponse(FacesContext context, TableData tableData, String fileName) {
        boolean binaryContent = isBinaryContent();
        StringWriter stringWriter = !binaryContent ? new StringWriter() : null;
        ByteArrayOutputStream byteArrayOutputStream = binaryContent ? new ByteArrayOutputStream() : null;
        PrintWriter printWriter = new PrintWriter(stringWriter);
        writeFileContent(tableData, printWriter, byteArrayOutputStream);
        printWriter.flush();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        response.setContentType(getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        if (!binaryContent) {
            String stringFileContent = stringWriter.toString();
            PrintWriter responseWriter;
            try {
                responseWriter = response.getWriter();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            responseWriter.write(stringFileContent);
            responseWriter.close();
        } else {
            byte[] binaryFileContent = byteArrayOutputStream.toByteArray();
            OutputStream responseOutputStream;
            try {
                responseOutputStream = response.getOutputStream();
                responseOutputStream.write(binaryFileContent);
                responseOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        context.responseComplete();
    }

    /**
     * This content type will be specified for the response generated by the sendResponse method.
     * @return content type string, such as "text/csv", "text/plain", etc.
     */
    protected abstract String getContentType();

    /**
     * This method defines this exporter saves data into a text file or a binary file. This determines the type of
     * output that will be used for sending the file to the client agent: Writer (for text files), or OutputStream
     * (for binary files).
     * @return true if this exporter creates binary files, and false if it creates text files.
     */
    protected abstract boolean isBinaryContent();

    /**
     * Writes the data passed with tableData parameter to either writer or output stream, depending on the type of this
     * exporter (text or binary -- see the isBinaryContent method).
     * @param tableData    data that should be exported
     * @param writer       this writer should be used for text exporters. It is null if isBinaryContent() method
     *                     returns true;
     * @param outputStream this output stream should be used for binary exporters. It is null if isBinaryContent()
     *                     method returns false;
     */
    protected abstract void writeFileContent(TableData tableData, PrintWriter writer, OutputStream outputStream);

    /**
     * Specifies the default file extension, which is used if file name is not specified explicitly in the
     * AbstractTable.export function
     * @return the default file extension without the dot sign: "csv", "txt", etc.
     */
    public abstract String getDefaultFileExtension();
}
