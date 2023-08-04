CREATE TABLE invoice_table (
    invoice_id SERIAL PRIMARY KEY,
    uuid TEXT NOT NULL,
    pdf_data BYTEA NOT NULL
);
