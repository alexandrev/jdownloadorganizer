package com.xandrev.jdorg.organizers;

import com.xandrev.jdorg.audit.Audit;

/**
 * General interface of a new item organizer
 *
 * @author xandrev.com
 *
 */
public interface Organizer extends Comparable<Object>{

    /**
     * Method that indicate the priority of the organizer to priorize its
     * application
     *
     * @return priority. The low the better.
     */
    int getPriority();

    /**
     * Method that indicate the folder name which the item has to be located
     *
     * @param fileName item to relocate
     * @return folder name to relocate the item.
     */
    String generateFolder(String fileName);

    /**
     * Method that return the root folder for this organizer
     *
     * @return root folder name
     */
    String getRootFolder();

    void audit(Audit auditService, String origPath, String finalPath, boolean renamed, boolean copied, boolean deleted);
}
