
package ma.org.ormt.security.keycloak.services.realm;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakRealmServiceImpl implements KeycloakRealmService {

    @Override
    public RealmResource getRealmResource(Keycloak keycloak, String realmName) {
        return keycloak.realm(realmName);
    }

    @Override
    public boolean realmExists(Keycloak keycloak, String realmName) {
        return keycloak.realms().findAll().stream().anyMatch(realm -> realm.getRealm().equals(realmName));
    }

    @Override
    public RealmResource createRealm(Keycloak keycloak, String realmName) {
        if (realmExists(keycloak, realmName)) {
            return null;
        }
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(realmName);
        realmRepresentation.setEnabled(true);
        realmRepresentation.setLoginTheme(realmName);
        realmRepresentation.setEmailTheme(realmName);

        realmRepresentation.setAdminTheme(realmName);
        realmRepresentation.setAccountTheme(realmName);

        realmRepresentation.setInternationalizationEnabled(true);
        realmRepresentation.setSupportedLocales(Set.of("fr"));
        realmRepresentation.setDefaultLocale("fr");

        RequiredActionProviderRepresentation otpRequiredActions = getOtpRequiredActions(false);
        RequiredActionProviderRepresentation updatePasswordRequiredActions = getUpdatePasswordRequiredActions(true);
        realmRepresentation.setRequiredActions(List.of(otpRequiredActions, updatePasswordRequiredActions));

        keycloak.realms().create(realmRepresentation);

        return this.getRealmResource(keycloak, realmName);
    }

    private RequiredActionProviderRepresentation getOtpRequiredActions(boolean required) {
        RequiredActionProviderRepresentation otp = new RequiredActionProviderRepresentation();
        otp.setAlias("CONFIGURE_TOTP");
        otp.setEnabled(required);
        otp.setName("Configure OTP");
        otp.setProviderId("CONFIGURE_TOTP");
        otp.setPriority(10);
        otp.setDefaultAction(false);
        return otp;
    }

    private RequiredActionProviderRepresentation getUpdatePasswordRequiredActions(boolean required) {
        RequiredActionProviderRepresentation updatePassword = new RequiredActionProviderRepresentation();
        updatePassword.setAlias("UPDATE_PASSWORD");
        updatePassword.setEnabled(required);
        updatePassword.setName("Update Password");
        updatePassword.setProviderId("UPDATE_PASSWORD");
        updatePassword.setPriority(30);
        updatePassword.setDefaultAction(false);
        return updatePassword;
    }

    @Override
    public void deleteRealm(Keycloak keycloak, String realmName) {
        if (!realmExists(keycloak, realmName)) {
            return;
        }
        RealmResource realmResource = this.getRealmResource(keycloak, realmName);
        realmResource.remove();

    }

    @Override
    public Optional<ClientResource> getClientResource(Keycloak keycloak, String realm, String clientName) {
        RealmResource realmResource = this.getRealmResource(keycloak, realm);
        ClientRepresentation clientRepresentation = realmResource.clients()
                .findByClientId(clientName)
                .get(0);
        return Optional.ofNullable(realmResource.clients().get(clientRepresentation.getId()));
    }

}