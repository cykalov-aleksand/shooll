CREATE TABLE human ( 
    id SERIAL NOT NULL PRIMARY key,
    name TEXT NOT NULL,
    age INTEGER NOT NULL,
    rights BOOLEAN NOT NULL DEFAULT 'false',
    individ_number_car TEXT REFERENCES car (individ_number_car)
);

CREATE TABLE car ( 
    individ_number_car TEXT NOT NULL PRIMARY key,
    stamp TEXT NOT NULL,
    model TEXT NOT NULL,
    costCar INTEGER
    );

INSERT INTO public.car (individ_number_car, stamp, model, costCar) VALUES ('oa777o 77rus', 'Фольксваген', 'Пассат В5', 5000000);

INSERT INTO public.human (id, "name", age, rights, individ_number_car) VALUES (1,'Александр','23','true','oa777o 77rus');
