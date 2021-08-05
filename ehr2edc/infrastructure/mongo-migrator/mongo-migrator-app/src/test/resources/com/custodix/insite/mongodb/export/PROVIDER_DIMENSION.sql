insert into PROVIDER_DIMENSION
select distinct provider_id, 'provider_path','name_char', 'provider_blob',NOW(),NOW(),NOW(),'sourcesystem_cd',0
from OBSERVATION_FACT;