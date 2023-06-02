CREATE VIEW plants_usable(id, name, name_latin, family, life_form) as
SELECT "plant_info"."id",
       "plant_info"."name",
       "plant_info"."name_latin",
       "families"."name",
       "life_forms"."name"
FROM plant_info
         LEFT JOIN "families" ON "plant_info"."family_id" = "families"."id"
         LEFT JOIN "life_forms" ON "plant_info"."life_form_id" = "life_forms"."id";

CREATE VIEW care_usable(id, plant_id, plant_name, care_date, care_type, note) as
SELECT care_history.id,
       care_history.id,
       plant_info."name",
       care_date,
       care_types."name",
       care_history.note
FROM care_history
         LEFT JOIN plant_info ON plant_info.id = care_history.plant_id
         LEFT JOIN care_types ON care_types.id = care_history.care_type_id;

CREATE VIEW disruptions_usable(id, plant_id, plant_name, disruptor, beginning_date, ending_date, note)
AS
SELECT disruptions_history.id,
       pi.id,
       pi."name",
       d."name",
       beginning_date,
       ending_date,
       disruptions_history.note
FROM disruptions_history
         LEFT JOIN plant_info pi on pi.id = disruptions_history.plant_id
         LEFT JOIN disruptors d on disruptions_history.disruptor_id = d.id;


create or replace procedure add_disruption(p_plant_id INT, p_plant_name VARCHAR, p_disruptor VARCHAR,
                                           p_beginning_date DATE, p_ending_date DATE, p_note VARCHAR)
    language plpgsql
as
$$
BEGIN
    INSERT INTO disruptions_history(plant_id, disruptor_id, beginning_date, ending_date, note)
    VALUES (p_plant_id,
            (SELECT id FROM disruptors WHERE disruptors."name" = p_disruptor),
            p_beginning_date,
            p_ending_date,
            p_note);
END
$$;

create procedure add_plant(IN name_p character varying, IN name_latin_p character varying, IN family_p character varying, IN life_form_p character varying)
    language plpgsql
as
$$
BEGIN
    INSERT INTO plant_info(name, name_latin, family_id, life_form_id)
    VALUES (name_p, name_latin_p,
            (SELECT id FROM families WHERE families.name = family_p),
            (SELECT id FROM life_forms WHERE life_forms.name = life_form_p)
           );
END
$$;

create procedure add_care(p_plant_id INT, p_plant_name VARCHAR, p_care_date timestamp,
                          p_care_type VARCHAR, p_note VARCHAR)
    language plpgsql
as
$$
BEGIN
    INSERT INTO care_history(plant_id, care_date, care_type_id, note)
    VALUES (p_plant_id,
            p_care_date,
            (SELECT id FROM care_types WHERE care_types.name = p_care_type),
            p_note
           );
END
$$;


create procedure update_plant(id_p INT, IN name_p character varying, IN name_latin_p character varying,
                              IN family_p character varying, IN life_form_p character varying)
    language plpgsql
as
$$
BEGIN
    UPDATE plant_info
    SET name         = name_p,
        name_latin   = name_latin_p,
        family_id    = (SELECT id FROM families WHERE families.name = family_p),
        life_form_id = (SELECT id FROM life_forms WHERE life_forms.name = life_form_p)
    WHERE id = id_p;
END
$$;

create or replace procedure update_care(id_p INT, p_plant_id INT, p_plant_name VARCHAR, p_care_date timestamp,
                                        p_care_type VARCHAR, p_note VARCHAR)
    language plpgsql
as
$$
BEGIN
    UPDATE care_history
    SET plant_id     = p_plant_id,
        care_date    = p_care_date,
        care_type_id = (SELECT id FROM care_types WHERE care_types.name = p_care_type),
        note         = p_note
    WHERE id = id_p;
END
$$;

create procedure update_disruption(p_id INT, p_plant_id INT, p_plant_name VARCHAR, p_disruptor VARCHAR,
                                   p_beginning_date DATE, p_ending_date DATE, p_note VARCHAR)
    language plpgsql
as
$$
BEGIN
    UPDATE disruptions_history
    SET plant_id         = p_plant_id,
        disruptor_id   = (SELECT disruptors.id FROM disruptors WHERE  disruptors.name = p_disruptor),
        beginning_date = p_beginning_date,
        ending_date = p_ending_date,
        note = p_note
    WHERE id = p_id;
END
$$;

