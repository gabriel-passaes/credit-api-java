-- Criando os usuários SUPER_ADMIN, ADMIN e USER
INSERT INTO user (id, name, email, document, role, avatar_url, created_at, updated_at, password)
VALUES
    (1, 'Super Admin', 'superadmin@empresa.com', '12345678900', 'SUPER_ADMIN', NULL, NOW(), NOW(), 'senha_superadmin'),
    (2, 'Admin User', 'admin@empresa.com', '98765432100', 'ADMIN', NULL, NOW(), NOW(), 'senha_admin'),
    (3, 'Regular User', 'user@empresa.com', '12312312300', 'USER', NULL, NOW(), NOW(), 'senha_user');

-- Inserindo 50 notas fiscais de crédito
DO $$ 
DECLARE
    i INT := 1;
BEGIN
    WHILE i <= 50 LOOP
        INSERT INTO credit (id, credit_number, nfse_number, constitution_date, issqn_amount, credit_type, simple_national, rate, billed_amount, deduction_amount, calculation_base, uploaded_file_name, uploaded_file_path, invoice_uploaded, user_id)
        VALUES
            (i, 
            CONCAT('CREDIT-', LPAD(i::TEXT, 3, '0')), 
            CONCAT('NFSE-', LPAD(i::TEXT, 3, '0')), 
            CURRENT_DATE, 
            1000 + (i * 10), 
            'ISSQN', 
            TRUE, 
            5, 
            2000 + (i * 100), 
            500 + (i * 10), 
            1500 + (i * 50), 
            NULL, 
            NULL, 
            FALSE, 
            CASE
                WHEN i = 1 THEN 1
                WHEN i = 2 THEN 2
                ELSE 3
            END
        );
        i := i + 1;
    END LOOP;
END $$;
