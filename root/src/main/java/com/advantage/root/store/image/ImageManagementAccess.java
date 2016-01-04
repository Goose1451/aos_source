package com.advantage.root.store.image;

import com.advantage.common.util.ArgumentValidationHelper;

import java.util.HashMap;
import java.util.Map;

public abstract class ImageManagementAccess {

    private static final Map<String, ImageManagement> imageManagementsMap = new HashMap<String, ImageManagement>();

    private ImageManagementAccess() {

        throw new UnsupportedOperationException();
    }

    public static ImageManagement getImageManagement(final String repositoryDirectoryPath) {

        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(repositoryDirectoryPath,
                "repository directory path");
        final ImageManagement imageManagement;

        synchronized (imageManagementsMap) {

            if (imageManagementsMap.containsKey(repositoryDirectoryPath)) {

                imageManagement = imageManagementsMap.get(repositoryDirectoryPath);
            } else {

                final ImageManagementFactory imageManagementFactory = ImageManagementFactory.getImageManagementFactory();
                imageManagement = imageManagementFactory.getImageManagement(repositoryDirectoryPath);
                imageManagementsMap.put(repositoryDirectoryPath, imageManagement);
            }
        }

        return imageManagement;
    }
}