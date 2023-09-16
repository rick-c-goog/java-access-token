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
    //GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
    
    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonCredentialPath)).createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
    credentials.refreshIfExpired();
     //AccessToken token = credentials.getAccessToken();
     // OR
     AccessToken token = credentials.refreshAccessToken();
     System.out.println(token.getTokenValue());

    // The url or target audience to obtain the ID token for.
    //String targetAudience = "https://example.com";

    //getIdTokenFromServiceAccount(jsonCredentialPath, targetAudience);
  }

  //}
}
// [END auth_cloud_idtoken_service_account]