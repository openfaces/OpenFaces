/*
 * OpenFaces - JSF Component Library 2.0
 * Copyright (C) 2007-2013, TeamDev Ltd.
 * licensing@openfaces.org
 * Unless agreed in writing the contents of this file are subject to
 * the GNU Lesser General Public License Version 2.1 (the "LGPL" License).
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * Please visit http://openfaces.org/licensing/ for more details.
 */

package org.openfaces.demo.beans.fileupload;

import org.openfaces.demo.beans.datatable.Book;
import org.openfaces.event.FileUploadItem;
import org.openfaces.event.FileUploadStatus;
import org.openfaces.event.UploadCompletionEvent;
import org.openfaces.util.Faces;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUploadBean implements Serializable {
    private List<FileUploadItem> uploadedFiles = new ArrayList<FileUploadItem>();
    private Map<String, Long> fileSizes = new HashMap<String, Long>();

    public FileUploadBean() {
    }

    public void uploadComplete(UploadCompletionEvent e) {
        List<FileUploadItem> files = e.getFiles();
        uploadedFiles.addAll(files);
        for (FileUploadItem item : files) {
            if (item.getFile() == null) {
                continue;
            } else if (!fileSizes.containsKey(item.getFile().getName())) {
                fileSizes.put(item.getFile().getName(), item.getFile().length());
            }
        }
        deleteFiles(files);
    }

    private void deleteFiles(List<FileUploadItem> files) {
        for (FileUploadItem fileUploadItem : files) {
            File file = fileUploadItem.getFile();
            // delete the file to save disk space on the server running the demo
            if (file != null)
                file.delete();
        }
    }

    public List<FileUploadItem> getUploadedFiles() {
        return uploadedFiles;
    }

    public String getFileSize() {
        FileUploadItem fileUploadItem = Faces.var("fileUploadItem", FileUploadItem.class);
        String result = getFileSizeStr(fileUploadItem);
        return result;
    }

    private String getFileSizeStr(FileUploadItem fileUploadItem) {
        File file = fileUploadItem.getFile();
        String result;
        if (file == null)
            result = "N/A";
        else {
            result = (int) Math.ceil(fileSizes.get(file.getName()) / 1024.0) + "Kb";
        }
        return result;
    }

    public void bookImageUploaded(UploadCompletionEvent uploadCompletionEvent) {
        Book book = Faces.var("book", Book.class);
        List<FileUploadItem> files = uploadCompletionEvent.getFiles();
        FileUploadItem fileUploadItem = files.get(0);
        book.setUploadedCoverImage(fileUploadItem);
        deleteFiles(files);
    }

    public String getBookImageFileSize() {
        Book book = Faces.var("book", Book.class);
        FileUploadItem uploadedCoverImage = book.getUploadedCoverImage();
        if (uploadedCoverImage == null) return "N/A";
        FileUploadStatus status = uploadedCoverImage.getStatus();
        switch (status) {
            case SUCCESSFUL:
                return String.valueOf(book.getBookCoverImage().getFileSize());
            case STOPPED:
                return "<Upload stopped>";
            case FAILED:
                return "<Upload failed>";
            case SIZE_LIMIT_EXCEEDED:
                return "<Size limit exceeded>";
            default:
                throw new IllegalStateException("Unknown FileUploadStatus enumeration value: " + status);
        }
    }

    public String getBookImageFileName() {
        Book book = Faces.var("book", Book.class);
        FileUploadItem uploadedCoverImage = book.getUploadedCoverImage();
        if (uploadedCoverImage == null) return "N/A";
        return uploadedCoverImage.getFileName();
    }

    public String getFileInfoClass() {
        Book book = Faces.var("book", Book.class);
        return book.getUploadedCoverImage() == null ? "notUploadedInfoText" : "uploadedInfoText";
    }
}
