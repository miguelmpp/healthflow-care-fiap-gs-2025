create table habitos_saude (

                               id bigint not null auto_increment,
                               ativo tinyint(1) not null default 1,

                               usuario_id bigint not null,

                               tipo_habito varchar(50) not null,
                               data_registro date not null,
                               duracao_minutos int not null,
                               nivel_cansaco int,
                               nivel_estresse int,
                               observacoes varchar(255),

                               primary key (id),

                               constraint fk_habito_usuario
                                   foreign key (usuario_id)
                                       references usuarios (id)
);
