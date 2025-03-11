CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(255) NOT NULL
);

CREATE TABLE repositories (
    repo_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repo_name VARCHAR(255) NOT NULL,
    repo_url VARCHAR(500) NOT NULL,
    provider VARCHAR(50) CHECK (provider IN ('github', 'gitlab', 'bitbucket')),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE contributors (
    cont_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE SET NULL,
    repo_id UUID REFERENCES repositories(repo_id) ON DELETE CASCADE
);

CREATE TABLE commits (
    comm_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cont_id UUID REFERENCES contributors(cont_id) ON DELETE CASCADE,
    commit_hash VARCHAR(50) UNIQUE NOT NULL,
    commit_message TEXT NOT NULL,
    commit_timestamp TIMESTAMP NOT NULL
);

CREATE TABLE commit_files (
    file_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    comm_id UUID REFERENCES commits(comm_id) ON DELETE CASCADE,
    file_path TEXT NOT NULL,
    change_type VARCHAR(50) CHECK (change_type in ('added', 'modified', 'deleted'))
);