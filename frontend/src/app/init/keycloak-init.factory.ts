import { KeycloakService } from "keycloak-angular";
import { collapseTextChangeRangesAcrossMultipleVersions } from "typescript";
import { ConfigInitService } from "./config-init.service";

// export function initializeKeycloak(
//   keycloak: KeycloakService,
//   configService: ConfigInitService
//   ) {
//     return () =>
//       keycloak.init({
//         config: {
//           url: configService.getKeycloakUrl(),
//           realm: configService.getKeycloakRealm(),
//           clientId: configService.getKeycloakClientId(),
//         }
//       });
// }

export function initializeKeycloak(
  keycloak: KeycloakService,
  configService: ConfigInitService
  ) {
    return () =>
      keycloak.init({
        config: {
          url: 'http://localhost:8080/auth',
          realm: 'test',
          clientId: 'frontend',
        }
      });
}

