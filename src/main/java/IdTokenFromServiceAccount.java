/*
 * Copyright 2022 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// [START auth_cloud_idtoken_service_account]

import com.google.auth.oauth2.IdToken;
import com.google.auth.oauth2.IdTokenProvider.Option;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class IdTokenFromServiceAccount {

  public static void main(String[] args)
      throws IOException, ExecutionException, InterruptedException, GeneralSecurityException {
    // TODO(Developer): Replace the below variables before running the code.

    // *NOTE*:
    // Using service account keys introduces risk; they are long-lived, and can be used by anyone
    // that obtains the key. Proper rotation and storage reduce this risk but do not eliminate it.
    // For these reasons, you should consider an alternative approach that
    // does not use a service account key. Several alternatives to service account keys
    // are described here:
    // https://cloud.google.com/docs/authentication/external/set-up-adc

    // Path to the service account json credential file.
    String jsonCredentialPath = "/home/admin_/access_token/rick-vertex-ai-key.json";
    
    String bearerToken="";
    
    //Option 1: use SA key Access token, with Json key file
    //bearerToken=getAccessTokenFromServiceAccount(jsonCredentialPath);
    
    // Option 2: Impersonate Service account get ID token, no need for Json key file
    String serviceAccount="vertex-ai-consumer@rick-vertex-ai.iam.gserviceaccount.com";
    String targetAudience = "https://example.com";
    String scope="https://www.googleapis.com/auth/cloud-platform";
    bearerToken= getIdTokenUsingOAuth2(serviceAccount,scope, targetAudience);

    //Option 3: Get Service Account ID token directly with key file
    // The url or target audience to obtain the ID token for Service Account
    //String targetAudience = "https://example.com";
    
    // bearerToken=getIdTokenFromServiceAccount(jsonCredentialPath, targetAudience);
    
}
  
  public static String getAccessTokenFromServiceAccount(String jsonCredentialPath)
      throws IOException {
    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonCredentialPath)).createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
    credentials.refreshIfExpired();
     //AccessToken token = credentials.getAccessToken();
     // OR
     AccessToken token = credentials.refreshAccessToken();
     String accessToken=token.getTokenValue();
     System.out.println(accessToken);
     return accessToken;

      }

  //alternative way to get ID token for a Service Account
  public static String getIdTokenFromServiceAccount(String jsonCredentialPath, String targetAudience)
      throws IOException {

    // Initialize the Service Account Credentials class with the path to the json file.
    ServiceAccountCredentials serviceAccountCredentials =
        ServiceAccountCredentials.fromStream(new FileInputStream(jsonCredentialPath));

    // Obtain the id token by providing the target audience.
    // tokenOption: Enum of various credential-specific options to apply to the token. Applicable
    // only for credentials obtained through Compute Engine or Impersonation.
    List<Option> tokenOption = Arrays.asList();
    IdToken idToken = serviceAccountCredentials.idTokenWithAudience(targetAudience, tokenOption);

    // The following method can also be used to generate the ID token.
    // IdTokenCredentials idTokenCredentials = IdTokenCredentials.newBuilder()
    //     .setIdTokenProvider(serviceAccountCredentials)
    //     .setTargetAudience(targetAudience)
    //     .build();

    String token = idToken.getTokenValue();
    System.out.println("Generated ID token:"+token);
    return token;
  }


  public static String getIdTokenUsingOAuth2(
      String impersonatedServiceAccount, String scope, String targetAudience) throws IOException {

    // Construct the GoogleCredentials object which obtains the default configuration from your
    // working environment.
    GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();

    // delegates: The chained list of delegates required to grant the final accessToken.
    // For more information, see:
    // https://cloud.google.com/iam/docs/create-short-lived-credentials-direct#sa-credentials-permissions
    // Delegate is NOT USED here.
    List<String> delegates = null;

    // Create the impersonated credential.
    ImpersonatedCredentials impersonatedCredentials =
        ImpersonatedCredentials.create(
            googleCredentials, impersonatedServiceAccount, delegates, Arrays.asList(scope), 300);

    // Set the impersonated credential, target audience and token options.
    IdTokenCredentials idTokenCredentials =
        IdTokenCredentials.newBuilder()
            .setIdTokenProvider(impersonatedCredentials)
            .setTargetAudience(targetAudience)
            // Setting this will include email in the id token.
            .setOptions(Arrays.asList(Option.INCLUDE_EMAIL))
            .build();

    // Get the ID token.
    // Once you've obtained the ID token, you can use it to make an authenticated call to the
    // target audience.

    AccessToken token = impersonatedCredentials.refreshAccessToken();
    String accessToken=token.getTokenValue();
    //String idToken=accessToken.getTokenValue();
    System.out.println("Generated ID token:"+accessToken);
    return accessToken;
  }

}
// [END auth_cloud_idtoken_service_account]