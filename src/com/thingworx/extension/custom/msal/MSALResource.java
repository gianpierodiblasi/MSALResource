package com.thingworx.extension.custom.msal;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IClientCertificate;
import com.thingworx.entities.utils.ThingUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxConfigurationTableDefinition;
import com.thingworx.metadata.annotations.ThingworxConfigurationTableDefinitions;
import com.thingworx.metadata.annotations.ThingworxDataShapeDefinition;
import com.thingworx.metadata.annotations.ThingworxFieldDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.resources.Resource;
import com.thingworx.things.repository.FileRepositoryThing;
import com.thingworx.types.ConfigurationTable;
import com.thingworx.types.collections.ValueCollection;
import java.io.FileInputStream;
import java.util.Collections;
import org.slf4j.Logger;

@ThingworxConfigurationTableDefinitions(tables = {
  @ThingworxConfigurationTableDefinition(name = "PFXCertificateParameters", description = "", isMultiRow = false, ordinal = 0, dataShape = @ThingworxDataShapeDefinition(fields = {
    @ThingworxFieldDefinition(name = "pfxCertificateFileRepository", description = "The repository containing the PCKS12 formatted certificate", baseType = "THINGNAME", ordinal = 1, aspects = {"isRequired:true", "thingTemplate:FileRepository"}),
    @ThingworxFieldDefinition(name = "pfxCertificatePath", description = "The path of the PCKS12 formatted certificate", baseType = "STRING", ordinal = 2, aspects = {"isRequired:true"}),
    @ThingworxFieldDefinition(name = "pfxCertificatePassword", description = "The password of the PCKS12 formatted certificate", baseType = "PASSWORD", ordinal = 3, aspects = {"isRequired:true"})}))})
public class MSALResource extends Resource {

  private final static Logger SCRIPT_LOGGER = LogUtilities.getInstance().getScriptLogger(MSALResource.class);
  private static final long serialVersionUID = 1L;

  @ThingworxServiceDefinition(name = "getAccessTokenByPFXCertificate", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false"})
  @ThingworxServiceResult(name = "response", description = "", baseType = "STRING")
  public String getAccessTokenByPFXCertificate(
          @ThingworxServiceParameter(name = "clientId", description = "The Client ID (Application ID) of the application as registered in the application registration portal (portal.azure.com)", baseType = "STRING", aspects = {"isRequired:true"}) String clientId,
          @ThingworxServiceParameter(name = "authority", description = "The URL of the authenticating authority or security token service (STS) from which acquire security tokens (ex. https://login.microsoftonline.com/{tenantId}/)", baseType = "STRING", aspects = {"isRequired:true"}) String authority,
          @ThingworxServiceParameter(name = "scope", description = "The scope application is requesting access to (ex. https://analysis.windows.net/powerbi/api/.default)", baseType = "STRING", aspects = {"isRequired:true"}) String scope) throws Exception {
    SCRIPT_LOGGER.debug("MSALResource - getAccessTokenByPFXCertificate -> Start");
    String accessToken;

    ConfigurationTable table = this.getConfigurationTable("PFXCertificateParameters");
    ValueCollection collection = table.getFirstRow();
    String pfxCertificateFileRepository = collection.getStringValue("pfxCertificateFileRepository");
    String pfxCertificatePath = collection.getStringValue("pfxCertificatePath");
    String pfxCertificatePassword = collection.getStringValue("pfxCertificatePassword");

    FileRepositoryThing FileRepositoryThing = (FileRepositoryThing) ThingUtilities.findThing(pfxCertificateFileRepository);
    try (FileInputStream certIs = FileRepositoryThing.openFileForRead(pfxCertificatePath)) {
      IClientCertificate clientCertificate = ClientCredentialFactory.createFromCertificate(certIs, pfxCertificatePassword != null && !pfxCertificatePassword.isEmpty() ? pfxCertificatePassword : "");
      ClientCredentialParameters clientCredentialParameters = ClientCredentialParameters.builder(Collections.singleton(scope)).build();
      ConfidentialClientApplication confidentialClientApplication = ConfidentialClientApplication.builder(clientId, clientCertificate).authority(authority).build();
      accessToken = confidentialClientApplication.acquireToken(clientCredentialParameters).get().accessToken();
    }

    SCRIPT_LOGGER.debug("MSALResource - getAccessTokenByPFXCertificate -> Stop");
    return accessToken;
  }
}
