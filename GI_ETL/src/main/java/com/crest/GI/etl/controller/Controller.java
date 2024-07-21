package com.crest.GI.etl.controller;

import com.dropbox.core.DbxException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public interface Controller {

  void ingestion(UUID pullUUID, UUID etlId, UUID integrationID, String path, String schemaName)
      throws IOException, SQLException, DbxException;
}
