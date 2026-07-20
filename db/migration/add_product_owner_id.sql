-- 상품-점주 소유권 체인 확보 — IDOR 취약점 수정 (이슈 #234)
-- 기존 데이터는 NULL로 유지 (단일 점주 시스템 backward compat)
ALTER TABLE tb_products
    ADD COLUMN owner_id BIGINT NULL;
