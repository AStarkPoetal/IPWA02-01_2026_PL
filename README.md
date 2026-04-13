# Require4Testing

Kompakter Prototyp einer Webanwendung zur Verwaltung von Requirements, Testfaellen, Testlaeufen, Testreports und Tasks.

## Technologie

- Java 17
- Maven
- JSF + CDI/Beans
- JPA + Hibernate
- MySQL
- Tomcat 10

## Funktionen

- Login mit Rollen aus der Datenbank
- Verwaltung von Requirements
- Verwaltung von Testfaellen
- Anlegen von Testlaeufen
- Zuordnung von Testfaellen und Tester:innen zu Testlaeufen
- Erfassung von Testreports
- Verwaltung von Tasks
- Rollenbasierte Freigabe von Funktionen

## Rollen

- `RE` Requirements Engineer
- `TM` Test Manager
- `TFE` Testfallersteller:in
- `T` Tester:in

Alle Module sind sichtbar. Nicht erlaubte Aktionen werden in der Oberfläche deaktiviert bzw. ausgegraut.

## Persistenz

Die Anwendung nutzt eine relationale MySQL-Datenbank. Die Persistenz wird mit JPA und Hibernate umgesetzt.

Wichtige Entitaeten:

- `User`
- `Requirement`
- `TestCase`
- `Test`
- `TestReport`
- `Task`

## Lokaler Start

Build:

```bash
mvn -DskipTests package
```

Deploy auf Tomcat:

```bash
cp target/Require4Testing_2026_LP_IPWA02-01-1.0-SNAPSHOT.war /opt/homebrew/opt/tomcat@10/libexec/webapps/require4testing.war
```

Aufruf:

```text
http://localhost:8080/require4testing/
```

## Test-Logins

- `re@test.com` / `12345`
- `tm@test.com` / `12345`
- `tfe@test.com` / `12345`
- `t@test.com` / `12345`

Weitere Tester-Accounts fuer Demo-Zwecke sind ebenfalls in der Datenbank vorhanden.

## Hinweis

Dies ist ein funktionaler Prototyp fuer das Modul Web-Development. Ausfuehrlichere Analyse, Designbeschreibung, UML und Screenshots werden separat in der Ausarbeitung dokumentiert.
