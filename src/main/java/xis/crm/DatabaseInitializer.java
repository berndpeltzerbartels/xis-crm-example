package xis.crm;

import one.xis.context.Component;
import one.xis.context.Init;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseInitializer {
    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Init
    void initialize() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            createSchema(connection);
            seed(connection);
            seedMissingReminders(connection);
        }
    }

    private void createSchema(Connection connection) throws SQLException {
        statement(connection, """
                create table if not exists employees (
                    id identity primary key,
                    name varchar(120) not null,
                    username varchar(60) unique not null,
                    password varchar(80) not null,
                    role varchar(20) not null,
                    active boolean not null
                )
                """);
        statement(connection, """
                create table if not exists customers (
                    id identity primary key,
                    name varchar(160) not null,
                    segment varchar(80) not null,
                    city varchar(80) not null,
                    email varchar(160) not null,
                    phone varchar(60) not null,
                    stage varchar(60) not null,
                    revenue int not null,
                    owner_id bigint not null,
                    notes clob not null,
                    constraint fk_customer_owner foreign key (owner_id) references employees(id)
                )
                """);
        statement(connection, """
                create table if not exists contacts (
                    id identity primary key,
                    customer_id bigint not null,
                    employee_id bigint not null,
                    contact_date date not null,
                    channel varchar(40) not null,
                    description clob not null,
                    constraint fk_contact_customer foreign key (customer_id) references customers(id),
                    constraint fk_contact_employee foreign key (employee_id) references employees(id)
                )
                """);
        statement(connection, """
                create table if not exists follow_ups (
                    id identity primary key,
                    customer_id bigint not null,
                    employee_id bigint not null,
                    due_date date not null,
                    task varchar(180) not null,
                    reminder varchar(80) not null,
                    done boolean not null,
                    constraint fk_follow_customer foreign key (customer_id) references customers(id),
                    constraint fk_follow_employee foreign key (employee_id) references employees(id)
                )
                """);
        statement(connection, """
                create table if not exists follow_up_reminders (
                    id identity primary key,
                    follow_up_id bigint not null,
                    remind_at timestamp not null,
                    label varchar(80) not null,
                    sent boolean not null,
                    constraint fk_reminder_follow_up foreign key (follow_up_id) references follow_ups(id)
                )
                """);
    }

    private void seed(Connection connection) throws SQLException {
        if (count(connection, "employees") > 0) {
            return;
        }
        statement(connection, """
                insert into employees (name, username, password, role, active) values
                ('Ada Keller', 'admin', 'admin', 'ADMIN', true),
                ('Mara Stein', 'mara', 'demo', 'SALES', true),
                ('Tom Berger', 'tom', 'demo', 'SALES', true)
                """);
        statement(connection, """
                insert into customers (name, segment, city, email, phone, stage, revenue, owner_id, notes) values
                ('Nordlicht Logistics', 'Enterprise', 'Hamburg', 'ops@nordlicht.example', '+49 40 100200', 'Proposal', 180000, 2, 'Needs rollout plan for three warehouses. Procurement asks for short implementation risk note.'),
                ('Kanzlei Winter & Partner', 'Professional Services', 'Koeln', 'office@winter.example', '+49 221 22233', 'Discovery', 42000, 3, 'Interested in secure client portal. Decision after tax season.'),
                ('Havel Energie', 'Public Sector', 'Potsdam', 'it@havel.example', '+49 331 908070', 'Negotiation', 260000, 2, 'Strong technical fit. Legal wants data retention matrix.'),
                ('Blue Pepper Retail', 'SMB', 'Leipzig', 'hello@bluepepper.example', '+49 341 8877', 'Onboarding', 28000, 3, 'Pilot starts with five shops. Keep training material compact.')
                """);
        statement(connection, """
                insert into contacts (customer_id, employee_id, contact_date, channel, description) values
                (1, 2, current_date - 4, 'Call', 'Discussed integration timeline and demo environment.'),
                (1, 1, current_date - 2, 'Email', 'Sent draft security answers and commercial outline.'),
                (2, 3, current_date - 8, 'Meeting', 'Collected current pain points around document handover.'),
                (3, 2, current_date - 1, 'Call', 'Procurement asked for final offer by Friday.'),
                (4, 3, current_date - 5, 'Workshop', 'Mapped shop manager onboarding flow.')
                """);
        statement(connection, """
                insert into follow_ups (customer_id, employee_id, due_date, task, reminder, done) values
                (1, 2, current_date + 1, 'Send revised offer with rollout phases', '09:00', false),
                (2, 3, current_date + 5, 'Schedule portal demo with partners', '14:30', false),
                (3, 2, current_date + 2, 'Send legal data retention matrix', '11:00', false),
                (4, 3, current_date + 7, 'Prepare training checklist', '10:00', false),
                (4, 3, current_date - 1, 'Confirm pilot store list', 'done', true)
                """);
    }

    private void seedMissingReminders(Connection connection) throws SQLException {
        if (count(connection, "follow_up_reminders") > 0) {
            return;
        }
        statement(connection, """
                insert into follow_up_reminders (follow_up_id, remind_at, label, sent)
                select id,
                       dateadd('minute',
                               cast(substring(reminder, 4, 2) as int),
                               dateadd('hour', cast(substring(reminder, 1, 2) as int), cast(due_date as timestamp))),
                       'Due date ' || reminder,
                       false
                from follow_ups
                where done = false and length(reminder) = 5
                """);
    }

    private int count(Connection connection, String table) throws SQLException {
        try (var statement = connection.createStatement();
             var result = statement.executeQuery("select count(*) from " + table)) {
            result.next();
            return result.getInt(1);
        }
    }

    private void statement(Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
