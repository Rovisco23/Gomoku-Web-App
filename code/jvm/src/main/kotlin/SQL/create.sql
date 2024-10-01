drop table if exists rankings;
drop table if exists games;
drop table if exists lobbies;
drop table if exists tokens;
drop table if exists users;

create table users(
    id       serial,
    username varchar(50) unique,
    password varchar(50),
    email    varchar(50) unique,
    primary key (id),
    constraint Email_Format check (email like '%@%.%')
);

create table tokens(
    token_validation VARCHAR(256),
    user_id int,
    created_at bigint not null,
    last_used_at bigint not null,
    primary key (user_id, token_validation),
    constraint User_ID foreign key (user_id) references users (id)
);

create table games(
    id         varchar(50),
    board      varchar(400),
    game_state varchar(8),
    rule_set   varchar(10),
    player_b   integer,
    player_w   integer,
    winner     integer,
    primary key (id, player_b, player_w),
    constraint Player_W_ID foreign key (player_w) references users (id),
    constraint Player_B_ID foreign key (player_b) references users (id),
    constraint Winner_ID check (winner in (player_b, player_w, null)),
    constraint Game_State check (game_state in ('Starting','On Going', 'Finished')),
    constraint Rule_Set check (rule_set in ('Free Style', 'Pro', 'Long Pro', 'Swap'))
);

create table rankings(
    user_id      integer not null,
    games_played integer,
    games_won    integer,
    games_drawn  integer,
    primary key (user_id),
    constraint User_ID foreign key (user_id) references users (id)
);

create table lobbies(
  user_id integer not null,
  rule_set varchar(10),
  boardSize integer,
  status varchar(8),
  game_id varchar(50),
  primary key (user_id),
  constraint User_ID foreign key (user_id) references users (id),
  constraint Status check (status in ('Not Full', 'Full')),
  constraint Rule_Set check (rule_set in ('Free Style', 'Pro', 'Long Pro', 'Swap'))
);

-- User triggers

create or replace function insert_into_rankings()
    returns trigger
    language plpgsql as
$$
BEGIN
    insert into rankings(user_id, games_played, games_won, games_drawn) values (new.id, 0, 0, 0);
    return null;
end;
$$;

create trigger insert_into_rankings_trigger
    after insert
    on users
    for each row
execute function insert_into_rankings();

-- Game triggers

create
    or replace function update_rankings()
    returns trigger
    language plpgsql as
$$
BEGIN
    if new.winner is null then
        update rankings set games_drawn = games_drawn + 1 where (user_id = new.player_b or user_id = new.player_w);
    else
        update rankings set games_won = games_won + 1 where (user_id = new.winner);
    end if;
    update rankings set games_played = games_played + 1 where (user_id = new.player_b or user_id = new.player_w);
    return null;
end;
$$;

create trigger update_rankings_trigger
    after update
    on games
    for each row
    when (old.game_state = 'On Going' and new.game_state = 'Finished')
execute function update_rankings();
