-- ============================================
-- DANE POCZĄTKOWE - System głosowania
-- ============================================
--
-- UWAGI:
-- - Wszystkie hasła: "password123"
-- - BCrypt hash (strength 12): $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe
-- - PESEL testowe (nie sprawdzane pod kątem poprawności sumy kontrolnej)
-- ============================================

-- ============================================
-- SEKCJA 1: UŻYTKOWNICY (VOTERS)
-- ============================================

-- Administrator systemu
INSERT INTO voter (email, password, first_name, last_name, role, active, created_at, updated_at)
VALUES
    ('admin@voting.pl',
     '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
     'Admin', 'System', 'ROLE_ADMIN', true,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Wyborcy testowi (aktywni)
INSERT INTO voter (email, password, first_name, last_name, pesel, role, active, created_at, updated_at)
VALUES
-- Grupa 1: Mieszkańcy którzy głosowali w wyborach na Wójta
('jan.kowalski@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Jan', 'Kowalski', '90010112345', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('anna.nowak@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Anna', 'Nowak', '85020298765', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('piotr.wisniewski@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Piotr', 'Wiśniewski', '92030387654', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('maria.wojcik@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Maria', 'Wójcik', '88040465432', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('tomasz.kaminski@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Tomasz', 'Kamiński', '91050543210', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('katarzyna.lewandowska@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Katarzyna', 'Lewandowska', '89060676543', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('andrzej.zielinski@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Andrzej', 'Zieliński', '87070765432', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('magdalena.szymanska@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Magdalena', 'Szymańska', '93080898765', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('robert.wozniak@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Robert', 'Woźniak', '86090987654', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('ewa.dqbrowski@example.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
 'Ewa', 'Dąbrowski', '94011076543', 'ROLE_VOTER', true,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Grupa 2: Mieszkańcy którzy się zarejestrowali ale nie głosowali
INSERT INTO voter (email, password, first_name, last_name, pesel, role, active, created_at, updated_at)
VALUES
    ('krzysztof.pawlak@example.com',
     '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
     'Krzysztof', 'Pawlak', '82120154321', 'ROLE_VOTER', true,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('monika.kaczmarek@example.com',
     '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
     'Monika', 'Kaczmarek', '95030265432', 'ROLE_VOTER', true,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('pawel.mazur@example.com',
     '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
     'Paweł', 'Mazur', '83040343210', 'ROLE_VOTER', true,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Grupa 3: Użytkownik zablokowany
INSERT INTO voter (email, password, first_name, last_name, pesel, role, active, created_at, updated_at)
VALUES
    ('blocked.user@example.com',
     '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyE.3O5qGhhe',
     'Zablokowany', 'Użytkownik', '84010187654', 'ROLE_VOTER', false,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- SEKCJA 2: WYBORY (ELECTIONS)
-- ============================================

-- Wybory zakończone (CLOSED)
INSERT INTO election (name, description, start_date, end_date, status, created_at, updated_at)
VALUES
    ('Wybory na Wójta Gminy 2025',
     'Wybory na stanowisko Wójta Gminy Przykładowa w kadencji 2025-2029. Głosowanie odbyło się 1 grudnia 2025 r. w godzinach 8:00-20:00.',
     '2025-12-01 08:00:00', '2025-12-01 20:00:00', 'CLOSED',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Wybory aktywne (ACTIVE)
INSERT INTO election (name, description, start_date, end_date, status, created_at, updated_at)
VALUES
    ('Referendum ws. budowy aquaparku',
     'Konsultacje społeczne w sprawie budowy aquaparku miejskiego. Szacowany koszt inwestycji: 15 000 000 zł. Planowane źródła finansowania: budżet gminy 40%, fundusze UE 50%, kredyt 10%.',
     '2025-12-15 08:00:00', '2025-12-20 20:00:00', 'ACTIVE',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Wybory w przygotowaniu (DRAFT)
INSERT INTO election (name, description, start_date, end_date, status, created_at, updated_at)
VALUES
    ('Budżet obywatelski 2026',
     'Wybierz projekt do realizacji w ramach budżetu obywatelskiego na rok 2026. Pula środków: 500 000 zł. Projekt z największą liczbą głosów zostanie zrealizowany w II kwartale 2026 r.',
     '2026-01-10 08:00:00', '2026-01-20 20:00:00', 'DRAFT',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Wybory do Rady Osiedla Słoneczne',
     'Wybory przedstawicieli mieszkańców Osiedla Słoneczne do Rady Osiedla na kadencję 2026-2028. Do wyboru 5 przedstawicieli spośród 13 kandydatów.',
     '2026-02-15 08:00:00', '2026-02-15 18:00:00', 'DRAFT',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- SEKCJA 3: OPCJE WYBORCZE (ELECTION_OPTIONS)
-- ============================================

-- Opcje dla: Wybory na Wójta Gminy 2025 (election_id = 1)
INSERT INTO election_option (election_id, option_title, description, display_order, created_at)
VALUES
    (1, 'Maria Kowalczyk',
     'Kandydatka niezależna, 52 lata. Wykształcenie wyższe (administracja). 15 lat doświadczenia w administracji samorządowej (skarbnik gminy 2010-2020, Z-ca Wójta 2020-2025). Program wyborczy: modernizacja infrastruktury drogowej, wsparcie lokalnej przedsiębiorczości, rozwój odnawialnych źródeł energii.',
     1, CURRENT_TIMESTAMP),

    (2, 'Tomasz Nowicki',
     'Kandydat Koalicji Obywatelskiej, 58 lat. Wykształcenie wyższe (prawo). Wójt gminy w latach 2015-2023. Program wyborczy: kontynuacja rozpoczętych inwestycji, rozwój edukacji i kultury, poprawa opieki zdrowotnej.',
     2, CURRENT_TIMESTAMP),

    (3, 'Andrzej Mazur',
     'Kandydat Prawa i Sprawiedliwości, 45 lat. Wykształcenie wyższe (zarządzanie). Lokalny przedsiębiorca (właściciel firmy budowlanej). Program wyborczy: obniżenie podatków lokalnych, wsparcie rodzin wielodzietnych, budowa żłobka gminnego.',
     3, CURRENT_TIMESTAMP);

-- Opcje dla: Referendum ws. budowy aquaparku (election_id = 2)
INSERT INTO election_option (election_id, option_title, description, display_order, created_at)
VALUES
    (2, 'TAK - popieram budowę aquaparku',
     'Jestem za budową aquaparku miejskiego. Inwestycja przyczyni się do rozwoju turystyki, utworzy nowe miejsca pracy (ok. 50 etatów) i zapewni atrakcję rekreacyjną dla mieszkańców. Planowana powierzchnia: 3000 m², baseny sportowe i rekreacyjne, strefa wellness.',
     1, CURRENT_TIMESTAMP),

    (2, 'NIE - jestem przeciw budowie aquaparku',
     'Jestem przeciw budowie aquaparku. Uważam, że środki finansowe (15 mln zł) powinny być przeznaczone na inne cele: remonty dróg, budowę chodników, termomodernizację budynków publicznych. Koszt utrzymania aquaparku będzie obciążał budżet gminy.',
     2, CURRENT_TIMESTAMP);

-- Opcje dla: Budżet obywatelski 2026 (election_id = 3)
INSERT INTO election_option (election_id, option_title, description, display_order, created_at)
VALUES
    (3, 'Plac zabaw przy ul. Słonecznej',
     'Budowa nowoczesnego placu zabaw z certyfikowanymi urządzeniami dla dzieci w różnym wieku (2-12 lat). Nawierzchnia bezpieczna (płyty gumowe). Ogrodzenie, ławki, kosze na śmieci, oświetlenie LED. Koszt: 150 000 zł. Powierzchnia: 400 m². Beneficjenci: około 500 rodzin z pobliskiego osiedla.',
     1, CURRENT_TIMESTAMP),

    (3, 'Remont chodników w centrum miasta',
     'Kompleksowy remont chodników na długości 2 km w centrum miasta (ul. Główna, Rynek, ul. Kościelna). Nowa nawierzchnia z kostki brukowej, obniżone krawężniki, nowe przejścia dla pieszych. Koszt: 200 000 zł. Poprawa bezpieczeństwa pieszych, dostępność dla osób z niepełnosprawnościami.',
     2, CURRENT_TIMESTAMP),

    (3, 'Nasadzenia drzew w parku miejskim',
     'Nasadzenie około 100 drzew liściastych (lipy, klony, dęby) i 200 krzewów ozdobnych w parku miejskim. Utworzenie alejek spacerowych. Koszt: 80 000 zł. Poprawa jakości powietrza, zwiększenie bioróżnorodności, nowe tereny zielone do rekreacji.',
     3, CURRENT_TIMESTAMP),

    (3, 'Siłownia zewnętrzna przy szkole podstawowej',
     'Budowa siłowni zewnętrznej z 12 profesjonalnymi urządzeniami do ćwiczeń (orbitreki, wiosła, prasy). Montaż na utwardzonym podłożu, oświetlenie LED, tablica informacyjna z instrukcjami ćwiczeń. Koszt: 120 000 zł. Dostępna bezpłatnie 24/7 dla wszystkich mieszkańców.',
     4, CURRENT_TIMESTAMP);

-- Opcje dla: Wybory do Rady Osiedla Słoneczne (election_id = 4)
INSERT INTO election_option (election_id, option_title, description, display_order, created_at)
VALUES
    (4, 'Ewa Zielińska',
     '42 lata, aktywistka lokalna. Organizatorka corocznych festynów sąsiedzkich i kiermaszy świątecznych. Członkini poprzedniej Rady Osiedla (2020-2024). Program: więcej wydarzeń integracyjnych, poprawa bezpieczeństwa (monitoring), zieleń osiedlowa.',
     1, CURRENT_TIMESTAMP),

    (4, 'Krzysztof Dąbrowski',
     '39 lat, nauczyciel matematyki w lokalnej szkole podstawowej. Trener drużyny piłkarskiej młodzików. Program: bezpieczeństwo dzieci (fotoradary, progi zwalniające), place zabaw, świetlica środowiskowa, korepetycje dla dzieci.',
     2, CURRENT_TIMESTAMP),

    (4, 'Magdalena Kaczmarek',
     '37 lat, przedsiębiorca (właścicielka kawiarni "Pod Lipą" na osiedlu). Mama trojga dzieci. Inicjatorka programu "Czyste Osiedle" - comiesięczne sprzątanie okolicy. Program: wsparcie przedsiębiorczości, czyste osiedle, wymiana starych śmietników.',
     3, CURRENT_TIMESTAMP),

    (4, 'Robert Jankowski',
     '55 lat, inżynier budownictwa. Mieszka na osiedlu od 20 lat. Specjalista ds. termomodernizacji budynków. Program: termomodernizacja bloków, docieplenie budynków, wymiana oświetlenia na LED, monitoring zużycia energii.',
     4, CURRENT_TIMESTAMP);

-- ============================================
-- SEKCJA 4: REKORDY GŁOSOWAŃ (VOTING_RECORDS)
-- ============================================
-- Rekordy głosowań dla zakończonych wyborów (election_id = 1)

INSERT INTO voting_record (voter_id, election_id, voted_at)
VALUES
-- Rano (8:00 - 12:00) - 3 głosy
(2, 1, '2025-12-01 09:15:23'),  -- Jan Kowalski
(3, 1, '2025-12-01 10:42:18'),  -- Anna Nowak
(4, 1, '2025-12-01 11:28:45'),  -- Piotr Wiśniewski

-- Popołudnie (12:00 - 16:00) - 2 głosy
(5, 1, '2025-12-01 13:05:12'),  -- Maria Wójcik
(6, 1, '2025-12-01 15:33:56'),  -- Tomasz Kamiński

-- Wieczór (16:00 - 20:00) - 5 głosów (największy napływ)
(7, 1, '2025-12-01 16:48:31'),  -- Katarzyna Lewandowska
(8, 1, '2025-12-01 17:22:09'),  -- Andrzej Zieliński
(9, 1, '2025-12-01 18:15:44'),  -- Magdalena Szymańska
(10, 1, '2025-12-01 19:03:27'), -- Robert Woźniak
(11, 1, '2025-12-01 19:47:55'); -- Ewa Dąbrowski

-- Podsumowanie: 10 głosów oddanych, 3 wyborców nie wzięło udziału
-- Frekwencja: 10/13 = 76.92%

-- ============================================
-- SEKCJA 5: GŁOSY (VOTES) - ANONIMOWE
-- ============================================

INSERT INTO vote (election_id, election_option_id)
VALUES
-- 5 głosów na Marię Kowalczyk (option_id = 1) - 50%
(1, 1),
(1, 1),
(1, 1),
(1, 1),
(1, 1),

-- 3 głosy na Tomasza Nowickiego (option_id = 2) - 30%
(1, 2),
(1, 2),
(1, 2),

-- 2 głosy na Andrzeja Mazura (option_id = 3) - 20%
(1, 3),
(1, 3);

-- ============================================
-- WYNIK WYBORÓW NA WÓJTA:
-- ============================================
-- Maria Kowalczyk:  5 głosów (50.00%) ← WYGRANA
-- Tomasz Nowicki:   3 głosy (30.00%)
-- Andrzej Mazur:    2 głosy (20.00%)
-- ────────────────────────────────────
-- RAZEM:           10 głosów (100%)
-- Frekwencja:      10/13 wyborców (76.92%)
-- ============================================