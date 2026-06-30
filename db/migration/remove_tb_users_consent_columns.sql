-- tb_users에서 legacy 동의 컬럼 제거
-- 동의 상태는 user_consents / user_consent_history 테이블로 정규화됨 (#159)
ALTER TABLE tb_users
    DROP COLUMN IF EXISTS terms_agreed,
    DROP COLUMN IF EXISTS privacy_agreed,
    DROP COLUMN IF EXISTS age_agreed,
    DROP COLUMN IF EXISTS marketing_agreed,
    DROP COLUMN IF EXISTS push_agreed;
