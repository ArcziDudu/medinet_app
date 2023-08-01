CREATE TABLE pdf_table (
    id SERIAL PRIMARY KEY,
    uuid TEXT NOT NULL,
    pdf_data BYTEA NOT NULL
);
