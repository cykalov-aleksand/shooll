ALTER TABLE public.student
ADD CONSTRAINT age_constraint CHECK (age >=16);

ALTER TABLE public.student  DROP CONSTRAINT age_constraint;

ALTER TABLE public.student
ADD CONSTRAINT name_unique UNIQUE (name);

ALTER TABLE public.student  DROP CONSTRAINT name_unique;

ALTER TABLE public.faculty
ADD CONSTRAINT name_color_unique UNIQUE (name,color);

ALTER TABLE public.faculty DROP CONSTRAINT name_color_unique;

ALTER TABLE public.student
ALTER age SET DEFAULT 20;


