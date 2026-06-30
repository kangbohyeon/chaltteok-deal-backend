-- ============================================================
-- #159: tb_users legacy 동의 컬럼 제거
-- 동의 상태는 tb_user_consents / tb_user_consent_history 로 정규화됨
--
-- [배포 순서 필수]
--   Step 1: 코드 배포 (User.kt 필드 제거 버전) → 서비스 정상 확인
--   Step 2: 이 파일 실행 (DROP COLUMN)
-- Step 1 없이 Step 2를 먼저 실행하면 구 코드가 없어진 컬럼을 참조해 장애 발생
-- ============================================================

-- ① 롤백 대비: 컬럼 데이터 백업 (DROP 실행 30일 후 제거 예정)
CREATE TABLE IF NOT EXISTS tb_users_consent_backup AS
SELECT user_id, terms_agreed, privacy_agreed, age_agreed, marketing_agreed, push_agreed,
       created_at
FROM tb_users;

-- ② 기존 유저 백필: tb_user_consents 에 아직 레코드가 없는 경우만 INSERT
INSERT INTO tb_user_consents (user_id, consent_type, agreed, agreed_at, consent_uuid, created_at, updated_at)
SELECT user_id, 'TERMS', terms_agreed, created_at, UUID(), NOW(), NOW()
FROM tb_users
WHERE NOT EXISTS (
    SELECT 1 FROM tb_user_consents c WHERE c.user_id = tb_users.user_id AND c.consent_type = 'TERMS'
);

INSERT INTO tb_user_consents (user_id, consent_type, agreed, agreed_at, consent_uuid, created_at, updated_at)
SELECT user_id, 'PRIVACY', privacy_agreed, created_at, UUID(), NOW(), NOW()
FROM tb_users
WHERE NOT EXISTS (
    SELECT 1 FROM tb_user_consents c WHERE c.user_id = tb_users.user_id AND c.consent_type = 'PRIVACY'
);

INSERT INTO tb_user_consents (user_id, consent_type, agreed, agreed_at, consent_uuid, created_at, updated_at)
SELECT user_id, 'AGE', age_agreed, created_at, UUID(), NOW(), NOW()
FROM tb_users
WHERE NOT EXISTS (
    SELECT 1 FROM tb_user_consents c WHERE c.user_id = tb_users.user_id AND c.consent_type = 'AGE'
);

INSERT INTO tb_user_consents (user_id, consent_type, agreed, agreed_at, consent_uuid, created_at, updated_at)
SELECT user_id, 'MARKETING', marketing_agreed, created_at, UUID(), NOW(), NOW()
FROM tb_users
WHERE NOT EXISTS (
    SELECT 1 FROM tb_user_consents c WHERE c.user_id = tb_users.user_id AND c.consent_type = 'MARKETING'
);

INSERT INTO tb_user_consents (user_id, consent_type, agreed, agreed_at, consent_uuid, created_at, updated_at)
SELECT user_id, 'PUSH', push_agreed, created_at, UUID(), NOW(), NOW()
FROM tb_users
WHERE NOT EXISTS (
    SELECT 1 FROM tb_user_consents c WHERE c.user_id = tb_users.user_id AND c.consent_type = 'PUSH'
);

-- ③ 기존 유저 이력 백필: tb_user_consent_history 에 최초 동의 이력이 없는 경우만 INSERT
INSERT INTO tb_user_consent_history (user_id, consent_type, agreed, changed_at, history_uuid, created_at, updated_at)
SELECT uc.user_id, uc.consent_type, uc.agreed, uc.agreed_at, UUID(), NOW(), NOW()
FROM tb_user_consents uc
WHERE NOT EXISTS (
    SELECT 1 FROM tb_user_consent_history h WHERE h.user_id = uc.user_id AND h.consent_type = uc.consent_type
);

-- ④ 검증: 모든 유저에게 5개 동의 타입이 존재하는지 확인 (결과가 0이어야 안전)
-- 실행 후 아래 쿼리로 반드시 확인:
-- SELECT COUNT(*) AS missing_consents
-- FROM tb_users u
-- WHERE (SELECT COUNT(*) FROM tb_user_consents c WHERE c.user_id = u.user_id) < 5;

-- ⑤ legacy 컬럼 제거 (비가역 — 위 검증 확인 후 실행)
ALTER TABLE tb_users
    DROP COLUMN IF EXISTS terms_agreed,
    DROP COLUMN IF EXISTS privacy_agreed,
    DROP COLUMN IF EXISTS age_agreed,
    DROP COLUMN IF EXISTS marketing_agreed,
    DROP COLUMN IF EXISTS push_agreed;
