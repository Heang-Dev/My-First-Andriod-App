# Complete Data Models & Fields Specification

## Overview
This document provides a comprehensive list of all data models (entities) required for the Code Snippet Sharing Platform, including all fields, data types, constraints, relationships, and validation rules.

---

## Table of Contents
1. [User Management Models](#user-management-models)
2. [Snippet Models](#snippet-models)
3. [Team Models](#team-models)
4. [Social & Interaction Models](#social--interaction-models)
5. [Organization Models](#organization-models)
6. [Analytics & Tracking Models](#analytics--tracking-models)
7. [System & Admin Models](#system--admin-models)
8. [Notification Models](#notification-models)
9. [API & Integration Models](#api--integration-models)
10. [Model Relationships Diagram](#model-relationships-diagram)

---

## 1. User Management Models

### 1.1 User Model
**Purpose:** Core user account information

| Field Name | Data Type | Constraints | Description | Indexed |
|-----------|-----------|-------------|-------------|---------|
| id | UUID | PRIMARY KEY, NOT NULL, AUTO | Unique user identifier | Yes |
| username | VARCHAR(50) | UNIQUE, NOT NULL, LOWERCASE | Unique username (lowercase) | Yes |
| email | VARCHAR(255) | UNIQUE, NOT NULL, LOWERCASE | User email address | Yes |
| email_verified | BOOLEAN | DEFAULT FALSE | Email verification status | No |
| email_verified_at | TIMESTAMP | NULL | When email was verified | No |
| password_hash | VARCHAR(255) | NULL | Bcrypt hashed password (NULL for OAuth) | No |
| full_name | VARCHAR(255) | NULL | User's full name | No |
| bio | TEXT | NULL, MAX 500 chars | User biography/description | No |
| avatar_url | VARCHAR(500) | NULL | Profile picture URL | No |
| location | VARCHAR(100) | NULL | User location/city | No |
| company | VARCHAR(100) | NULL | Company/organization name | No |
| job_title | VARCHAR(100) | NULL | Job title/role | No |
| github_url | VARCHAR(255) | NULL, URL format | GitHub profile URL | No |
| twitter_url | VARCHAR(255) | NULL, URL format | Twitter profile URL | No |
| linkedin_url | VARCHAR(255) | NULL, URL format | LinkedIn profile URL | No |
| website_url | VARCHAR(255) | NULL, URL format | Personal website URL | No |
| is_admin | BOOLEAN | DEFAULT FALSE | Admin privilege flag | Yes |
| is_active | BOOLEAN | DEFAULT TRUE | Account active status | Yes |
| is_suspended | BOOLEAN | DEFAULT FALSE | Account suspension status | Yes |
| is_banned | BOOLEAN | DEFAULT FALSE | Account ban status | Yes |
| suspension_reason | TEXT | NULL | Reason for suspension/ban | No |
| suspended_until | TIMESTAMP | NULL | Suspension end date | No |
| profile_visibility | ENUM | 'public', 'private' | Profile visibility setting | No |
| show_email | BOOLEAN | DEFAULT FALSE | Show email on public profile | No |
| show_activity | BOOLEAN | DEFAULT TRUE | Show activity on profile | No |
| allow_forks | BOOLEAN | DEFAULT TRUE | Allow others to fork snippets | No |
| default_snippet_privacy | ENUM | 'public', 'private', 'team' | Default privacy for new snippets | No |
| language_preference | VARCHAR(10) | DEFAULT 'en' | UI language preference | No |
| theme_preference | ENUM | 'light', 'dark', 'auto' | UI theme preference | No |
| snippets_count | INTEGER | DEFAULT 0 | Total number of snippets | No |
| public_snippets_count | INTEGER | DEFAULT 0 | Number of public snippets | No |
| followers_count | INTEGER | DEFAULT 0 | Number of followers | No |
| following_count | INTEGER | DEFAULT 0 | Number of users following | No |
| total_views | BIGINT | DEFAULT 0 | Total views across all snippets | No |
| total_forks | INTEGER | DEFAULT 0 | Total forks across all snippets | No |
| reputation_score | INTEGER | DEFAULT 0 | User reputation/karma score | No |
| last_login_at | TIMESTAMP | NULL | Last successful login timestamp | Yes |
| last_login_ip | INET | NULL | Last login IP address | No |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Account creation timestamp | Yes |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp | Yes |
| deleted_at | TIMESTAMP | NULL | Soft delete timestamp | Yes |

**Indexes:**
- `idx_user_username` (username)
- `idx_user_email` (email)
- `idx_user_created_at` (created_at DESC)
- `idx_user_is_active` (is_active)
- `idx_user_deleted_at` (deleted_at)

**Validation Rules:**
- Username: 3-50 characters, alphanumeric + underscore/hyphen only
- Email: Valid email format
- Password: Minimum 8 characters, must contain uppercase, lowercase, number
- Bio: Maximum 500 characters
- URLs: Must be valid URL format

---

### 1.2 UserProfile Model
**Purpose:** Extended user profile information (separate for performance)

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Profile identifier |
| user_id | UUID | FOREIGN KEY (users.id), UNIQUE | Reference to user |
| timezone | VARCHAR(50) | NULL | User timezone (e.g., 'America/New_York') |
| date_format | VARCHAR(20) | DEFAULT 'YYYY-MM-DD' | Preferred date format |
| code_editor_theme | VARCHAR(50) | DEFAULT 'vs-dark' | Preferred code editor theme |
| show_line_numbers | BOOLEAN | DEFAULT TRUE | Show line numbers in code |
| font_size | INTEGER | DEFAULT 14, MIN 10, MAX 24 | Editor font size |
| tab_size | INTEGER | DEFAULT 4, MIN 2, MAX 8 | Tab size in spaces |
| auto_save_enabled | BOOLEAN | DEFAULT TRUE | Enable auto-save for snippets |
| email_on_comment | BOOLEAN | DEFAULT TRUE | Email on new comments |
| email_on_fork | BOOLEAN | DEFAULT TRUE | Email when snippet is forked |
| email_on_follow | BOOLEAN | DEFAULT TRUE | Email on new follower |
| email_on_team_invite | BOOLEAN | DEFAULT TRUE | Email on team invitation |
| email_weekly_digest | BOOLEAN | DEFAULT TRUE | Weekly activity digest email |
| push_notifications_enabled | BOOLEAN | DEFAULT FALSE | Enable push notifications |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Profile creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

---

### 1.3 UserSession Model
**Purpose:** Track active user sessions

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Session identifier |
| user_id | UUID | FOREIGN KEY (users.id) | Reference to user |
| session_token | VARCHAR(255) | UNIQUE, NOT NULL | Session token hash |
| ip_address | INET | NOT NULL | Session IP address |
| user_agent | TEXT | NOT NULL | Browser user agent |
| device_type | VARCHAR(50) | NULL | Device type (desktop, mobile, tablet) |
| browser | VARCHAR(100) | NULL | Browser name and version |
| os | VARCHAR(100) | NULL | Operating system |
| location | VARCHAR(255) | NULL | Geographic location (city, country) |
| is_active | BOOLEAN | DEFAULT TRUE | Session active status |
| last_activity_at | TIMESTAMP | NOT NULL | Last activity timestamp |
| expires_at | TIMESTAMP | NOT NULL | Session expiration time |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Session creation timestamp |

**Indexes:**
- `idx_session_user_id` (user_id)
- `idx_session_token` (session_token)
- `idx_session_expires_at` (expires_at)

---

### 1.4 OAuthProvider Model
**Purpose:** OAuth provider connections

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | OAuth connection identifier |
| user_id | UUID | FOREIGN KEY (users.id) | Reference to user |
| provider | ENUM | 'google', 'github', 'gitlab' | OAuth provider name |
| provider_user_id | VARCHAR(255) | NOT NULL | User ID from provider |
| access_token | TEXT | NULL, ENCRYPTED | OAuth access token (encrypted) |
| refresh_token | TEXT | NULL, ENCRYPTED | OAuth refresh token (encrypted) |
| token_expires_at | TIMESTAMP | NULL | Token expiration time |
| provider_email | VARCHAR(255) | NULL | Email from provider |
| provider_username | VARCHAR(255) | NULL | Username from provider |
| provider_avatar_url | VARCHAR(500) | NULL | Avatar URL from provider |
| is_primary | BOOLEAN | DEFAULT FALSE | Primary OAuth connection |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Connection creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Unique Constraint:** (provider, provider_user_id)

---

### 1.5 PasswordReset Model
**Purpose:** Password reset requests

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Reset request identifier |
| user_id | UUID | FOREIGN KEY (users.id) | Reference to user |
| token | VARCHAR(255) | UNIQUE, NOT NULL | Reset token (hashed) |
| ip_address | INET | NOT NULL | Request IP address |
| is_used | BOOLEAN | DEFAULT FALSE | Token usage status |
| used_at | TIMESTAMP | NULL | When token was used |
| expires_at | TIMESTAMP | NOT NULL | Token expiration time |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Request creation timestamp |

**Indexes:**
- `idx_password_reset_token` (token)
- `idx_password_reset_expires_at` (expires_at)

---

### 1.6 EmailVerification Model
**Purpose:** Email verification tokens

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Verification identifier |
| user_id | UUID | FOREIGN KEY (users.id) | Reference to user |
| email | VARCHAR(255) | NOT NULL | Email to verify |
| token | VARCHAR(255) | UNIQUE, NOT NULL | Verification token (hashed) |
| is_verified | BOOLEAN | DEFAULT FALSE | Verification status |
| verified_at | TIMESTAMP | NULL | Verification timestamp |
| expires_at | TIMESTAMP | NOT NULL | Token expiration time |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Token creation timestamp |

---

## 2. Snippet Models

### 2.1 Snippet Model
**Purpose:** Core snippet information

| Field Name | Data Type | Constraints | Description | Indexed |
|-----------|-----------|-------------|-------------|---------|
| id | UUID | PRIMARY KEY, NOT NULL | Unique snippet identifier | Yes |
| user_id | UUID | FOREIGN KEY (users.id) | Snippet owner | Yes |
| team_id | UUID | FOREIGN KEY (teams.id), NULL | Associated team (if team snippet) | Yes |
| title | VARCHAR(255) | NOT NULL | Snippet title | Yes (full-text) |
| description | TEXT | NULL, MAX 2000 chars | Snippet description | Yes (full-text) |
| code | TEXT | NOT NULL, MAX 100KB | Actual code content | Yes (full-text) |
| language | VARCHAR(50) | NOT NULL | Programming language | Yes |
| language_version | VARCHAR(20) | NULL | Language version (e.g., 'Python 3.11') | No |
| category | VARCHAR(100) | NULL | Category name | Yes |
| file_name | VARCHAR(255) | NULL | Optional filename with extension | No |
| file_size | INTEGER | COMPUTED | Code content size in bytes | No |
| lines_of_code | INTEGER | COMPUTED | Number of lines in code | No |
| privacy | ENUM | NOT NULL | 'public', 'private', 'team', 'unlisted' | Yes |
| slug | VARCHAR(300) | UNIQUE, NOT NULL | URL-friendly slug | Yes |
| version_number | INTEGER | DEFAULT 1 | Current version number | No |
| parent_snippet_id | UUID | FOREIGN KEY (snippets.id), NULL | Original snippet if forked | Yes |
| is_fork | BOOLEAN | DEFAULT FALSE | Is this a fork? | Yes |
| is_featured | BOOLEAN | DEFAULT FALSE | Featured by admin | Yes |
| is_template | BOOLEAN | DEFAULT FALSE | Available as template | No |
| allow_comments | BOOLEAN | DEFAULT TRUE | Allow comments on snippet | No |
| allow_forks | BOOLEAN | DEFAULT TRUE | Allow forking | No |
| license | VARCHAR(50) | NULL | License type (e.g., 'MIT', 'Apache 2.0') | No |
| view_count | INTEGER | DEFAULT 0 | Total views | No |
| unique_view_count | INTEGER | DEFAULT 0 | Unique user views | No |
| fork_count | INTEGER | DEFAULT 0 | Number of forks | No |
| favorite_count | INTEGER | DEFAULT 0 | Number of favorites | No |
| comment_count | INTEGER | DEFAULT 0 | Number of comments | No |
| share_count | INTEGER | DEFAULT 0 | Number of shares | No |
| download_count | INTEGER | DEFAULT 0 | Number of downloads | No |
| embed_count | INTEGER | DEFAULT 0 | Number of embeds | No |
| trending_score | FLOAT | DEFAULT 0.0 | Calculated trending score | Yes |
| quality_score | FLOAT | DEFAULT 0.0 | Code quality score (if analyzed) | No |
| last_viewed_at | TIMESTAMP | NULL | Last view timestamp | No |
| published_at | TIMESTAMP | NULL | Publication timestamp | Yes |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp | Yes |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp | Yes |
| deleted_at | TIMESTAMP | NULL | Soft delete timestamp | Yes |

**Indexes:**
- `idx_snippet_user_id` (user_id)
- `idx_snippet_team_id` (team_id)
- `idx_snippet_language` (language)
- `idx_snippet_category` (category)
- `idx_snippet_privacy` (privacy)
- `idx_snippet_slug` (slug)
- `idx_snippet_created_at` (created_at DESC)
- `idx_snippet_published_at` (published_at DESC)
- `idx_snippet_trending_score` (trending_score DESC)
- `idx_snippet_is_featured` (is_featured)
- `idx_snippet_is_fork` (is_fork)
- `idx_snippet_parent_snippet_id` (parent_snippet_id)

**Full-text Indexes:**
- `idx_snippet_title_fts` (to_tsvector('english', title))
- `idx_snippet_description_fts` (to_tsvector('english', description))
- `idx_snippet_code_fts` (to_tsvector('english', code))

**Validation Rules:**
- Title: 1-255 characters, not empty
- Code: Maximum 100KB
- Language: Must be from supported languages list
- Privacy: Must be one of enum values

---

### 2.2 SnippetVersion Model
**Purpose:** Version history for snippets

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Version identifier |
| snippet_id | UUID | FOREIGN KEY (snippets.id) | Reference to parent snippet |
| version_number | INTEGER | NOT NULL | Version number (1, 2, 3...) |
| title | VARCHAR(255) | NOT NULL | Title at this version |
| description | TEXT | NULL | Description at this version |
| code | TEXT | NOT NULL | Code at this version |
| language | VARCHAR(50) | NOT NULL | Language at this version |
| change_summary | TEXT | NULL | Summary of changes |
| change_type | ENUM | 'create', 'update', 'restore' | Type of change |
| lines_added | INTEGER | DEFAULT 0 | Lines added in this version |
| lines_removed | INTEGER | DEFAULT 0 | Lines removed in this version |
| created_by | UUID | FOREIGN KEY (users.id) | User who created this version |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Version creation timestamp |

**Unique Constraint:** (snippet_id, version_number)

**Indexes:**
- `idx_snippet_version_snippet_id` (snippet_id)
- `idx_snippet_version_created_at` (created_at DESC)

---

### 2.3 SnippetFile Model
**Purpose:** Multiple files within a snippet (for multi-file snippets)

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | File identifier |
| snippet_id | UUID | FOREIGN KEY (snippets.id) | Reference to snippet |
| file_name | VARCHAR(255) | NOT NULL | File name with extension |
| file_path | VARCHAR(500) | NULL | File path (for directory structure) |
| code | TEXT | NOT NULL | File content |
| language | VARCHAR(50) | NOT NULL | Programming language |
| order | INTEGER | DEFAULT 0 | Display order |
| is_main_file | BOOLEAN | DEFAULT FALSE | Primary file in snippet |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | File creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Unique Constraint:** (snippet_id, file_path, file_name)

---

### 2.4 SnippetMetadata Model
**Purpose:** Additional metadata for snippets

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Metadata identifier |
| snippet_id | UUID | FOREIGN KEY (snippets.id), UNIQUE | Reference to snippet |
| framework | VARCHAR(100) | NULL | Framework used (e.g., 'React', 'Spring Boot') |
| framework_version | VARCHAR(20) | NULL | Framework version |
| dependencies | JSONB | NULL | List of dependencies |
| environment | VARCHAR(50) | NULL | Environment (e.g., 'Node.js', 'JVM') |
| difficulty_level | ENUM | NULL | 'beginner', 'intermediate', 'advanced' |
| execution_time | VARCHAR(50) | NULL | Typical execution time |
| memory_usage | VARCHAR(50) | NULL | Typical memory usage |
| use_cases | TEXT[] | NULL | Array of use cases |
| keywords | TEXT[] | NULL | Additional keywords for search |
| external_links | JSONB | NULL | Related external resources |
| seo_title | VARCHAR(255) | NULL | SEO optimized title |
| seo_description | TEXT | NULL | SEO meta description |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Metadata creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

---

## 3. Team Models

### 3.1 Team Model
**Purpose:** Team/organization information

| Field Name | Data Type | Constraints | Description | Indexed |
|-----------|-----------|-------------|-------------|---------|
| id | UUID | PRIMARY KEY, NOT NULL | Team identifier | Yes |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Team name | Yes |
| slug | VARCHAR(120) | UNIQUE, NOT NULL | URL-friendly slug | Yes |
| description | TEXT | NULL, MAX 1000 chars | Team description | No |
| avatar_url | VARCHAR(500) | NULL | Team avatar/logo URL | No |
| banner_url | VARCHAR(500) | NULL | Team banner image URL | No |
| website_url | VARCHAR(255) | NULL | Team website URL | No |
| privacy | ENUM | NOT NULL | 'public', 'private', 'invite_only' | Yes |
| owner_id | UUID | FOREIGN KEY (users.id) | Team owner | Yes |
| member_count | INTEGER | DEFAULT 1 | Total number of members | No |
| snippet_count | INTEGER | DEFAULT 0 | Total number of team snippets | No |
| visibility | ENUM | DEFAULT 'public' | 'public', 'members_only', 'private' | No |
| allow_member_invite | BOOLEAN | DEFAULT TRUE | Members can invite others | No |
| default_snippet_privacy | ENUM | DEFAULT 'team' | Default privacy for team snippets | No |
| require_approval | BOOLEAN | DEFAULT FALSE | Require approval for join requests | No |
| max_members | INTEGER | NULL | Maximum number of members (NULL = unlimited) | No |
| is_verified | BOOLEAN | DEFAULT FALSE | Verified team badge | No |
| is_active | BOOLEAN | DEFAULT TRUE | Team active status | Yes |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Team creation timestamp | Yes |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp | Yes |
| deleted_at | TIMESTAMP | NULL | Soft delete timestamp | Yes |

**Indexes:**
- `idx_team_name` (name)
- `idx_team_slug` (slug)
- `idx_team_owner_id` (owner_id)
- `idx_team_privacy` (privacy)
- `idx_team_created_at` (created_at DESC)

---

### 3.2 TeamMember Model
**Purpose:** Team membership and roles

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Membership identifier |
| team_id | UUID | FOREIGN KEY (teams.id) | Reference to team |
| user_id | UUID | FOREIGN KEY (users.id) | Reference to user |
| role | ENUM | NOT NULL | 'owner', 'admin', 'moderator', 'member', 'viewer' |
| custom_title | VARCHAR(100) | NULL | Custom role title |
| permissions | JSONB | NULL | Custom permissions object |
| can_create_snippets | BOOLEAN | DEFAULT TRUE | Permission to create snippets |
| can_edit_snippets | BOOLEAN | DEFAULT FALSE | Permission to edit all snippets |
| can_delete_snippets | BOOLEAN | DEFAULT FALSE | Permission to delete snippets |
| can_manage_members | BOOLEAN | DEFAULT FALSE | Permission to manage members |
| can_invite_members | BOOLEAN | DEFAULT TRUE | Permission to invite members |
| is_active | BOOLEAN | DEFAULT TRUE | Active membership status |
| invited_by | UUID | FOREIGN KEY (users.id), NULL | User who invited |
| invitation_accepted_at | TIMESTAMP | NULL | When invitation was accepted |
| joined_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Join timestamp |
| left_at | TIMESTAMP | NULL | Leave timestamp |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Unique Constraint:** (team_id, user_id)

**Indexes:**
- `idx_team_member_team_id` (team_id)
- `idx_team_member_user_id` (user_id)
- `idx_team_member_role` (role)

---

### 3.3 TeamInvitation Model
**Purpose:** Pending team invitations

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Invitation identifier |
| team_id | UUID | FOREIGN KEY (teams.id) | Reference to team |
| invited_by | UUID | FOREIGN KEY (users.id) | User who sent invitation |
| email | VARCHAR(255) | NOT NULL | Invited email address |
| user_id | UUID | FOREIGN KEY (users.id), NULL | User ID if registered |
| role | ENUM | DEFAULT 'member' | Intended role |
| token | VARCHAR(255) | UNIQUE, NOT NULL | Invitation token |
| message | TEXT | NULL | Personal message |
| status | ENUM | DEFAULT 'pending' | 'pending', 'accepted', 'declined', 'expired' |
| accepted_at | TIMESTAMP | NULL | Acceptance timestamp |
| declined_at | TIMESTAMP | NULL | Decline timestamp |
| expires_at | TIMESTAMP | NOT NULL | Expiration timestamp |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Invitation creation timestamp |

**Indexes:**
- `idx_team_invitation_team_id` (team_id)
- `idx_team_invitation_email` (email)
- `idx_team_invitation_token` (token)
- `idx_team_invitation_status` (status)

---

### 3.4 TeamRole Model
**Purpose:** Custom team roles (for advanced permission management)

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Role identifier |
| team_id | UUID | FOREIGN KEY (teams.id) | Reference to team |
| name | VARCHAR(100) | NOT NULL | Role name |
| description | TEXT | NULL | Role description |
| permissions | JSONB | NOT NULL | Permissions object |
| color | VARCHAR(7) | NULL | Role badge color (hex) |
| is_default | BOOLEAN | DEFAULT FALSE | Default role for new members |
| position | INTEGER | DEFAULT 0 | Display order |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Role creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Unique Constraint:** (team_id, name)

---

## 4. Social & Interaction Models

### 4.1 Favorite Model
**Purpose:** User favorites/bookmarks

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Favorite identifier |
| user_id | UUID | FOREIGN KEY (users.id) | User who favorited |
| snippet_id | UUID | FOREIGN KEY (snippets.id) | Favorited snippet |
| note | TEXT | NULL, MAX 500 chars | Personal note about favorite |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Favorite creation timestamp |

**Unique Constraint:** (user_id, snippet_id)

**Indexes:**
- `idx_favorite_user_id` (user_id)
- `idx_favorite_snippet_id` (snippet_id)
- `idx_favorite_created_at` (created_at DESC)

---

### 4.2 Comment Model
**Purpose:** Comments on snippets

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Comment identifier |
| snippet_id | UUID | FOREIGN KEY (snippets.id) | Commented snippet |
| user_id | UUID | FOREIGN KEY (users.id) | Comment author |
| parent_comment_id | UUID | FOREIGN KEY (comments.id), NULL | Parent comment (for replies) |
| content | TEXT | NOT NULL, MAX 2000 chars | Comment content |
| line_number | INTEGER | NULL | Specific line number (for inline comments) |
| is_edited | BOOLEAN | DEFAULT FALSE | Has been edited |
| edited_at | TIMESTAMP | NULL | Last edit timestamp |
| upvote_count | INTEGER | DEFAULT 0 | Number of upvotes |
| reply_count | INTEGER | DEFAULT 0 | Number of replies |
| is_pinned | BOOLEAN | DEFAULT FALSE | Pinned by author/admin |
| is_resolved | BOOLEAN | DEFAULT FALSE | Marked as resolved (for issues) |
| resolved_by | UUID | FOREIGN KEY (users.id), NULL | User who resolved |
| resolved_at | TIMESTAMP | NULL | Resolution timestamp |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Comment creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |
| deleted_at | TIMESTAMP | NULL | Soft delete timestamp |

**Indexes:**
- `idx_comment_snippet_id` (snippet_id)
- `idx_comment_user_id` (user_id)
- `idx_comment_parent_id` (parent_comment_id)
- `idx_comment_created_at` (created_at DESC)

---

### 4.3 CommentVote Model
**Purpose:** Upvotes/downvotes on comments

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Vote identifier |
| comment_id | UUID | FOREIGN KEY (comments.id) | Voted comment |
| user_id | UUID | FOREIGN KEY (users.id) | User who voted |
| vote_type | ENUM | NOT NULL | 'upvote', 'downvote' |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Vote creation timestamp |

**Unique Constraint:** (comment_id, user_id)

---

### 4.4 Follow Model
**Purpose:** User following relationships

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Follow relationship identifier |
| follower_id | UUID | FOREIGN KEY (users.id) | User who is following |
| following_id | UUID | FOREIGN KEY (users.id) | User being followed |
| notification_enabled | BOOLEAN | DEFAULT TRUE | Receive notifications from this user |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Follow timestamp |

**Unique Constraint:** (follower_id, following_id)

**Indexes:**
- `idx_follow_follower_id` (follower_id)
- `idx_follow_following_id` (following_id)

**Validation:**
- follower_id ≠ following_id (can't follow yourself)

---

### 4.5 Fork Model
**Purpose:** Track snippet forks (separate from parent_snippet_id for detailed tracking)

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Fork identifier |
| original_snippet_id | UUID | FOREIGN KEY (snippets.id) | Original snippet |
| forked_snippet_id | UUID | FOREIGN KEY (snippets.id) | Forked snippet |
| forked_by | UUID | FOREIGN KEY (users.id) | User who forked |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Fork timestamp |

**Indexes:**
- `idx_fork_original_snippet_id` (original_snippet_id)
- `idx_fork_forked_snippet_id` (forked_snippet_id)
- `idx_fork_forked_by` (forked_by)

---

### 4.6 Share Model
**Purpose:** Track snippet shares

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Share identifier |
| snippet_id | UUID | FOREIGN KEY (snippets.id) | Shared snippet |
| user_id | UUID | FOREIGN KEY (users.id), NULL | User who shared (NULL if anonymous) |
| share_method | ENUM | NOT NULL | 'link', 'embed', 'email', 'twitter', 'linkedin', 'facebook' |
| platform | VARCHAR(50) | NULL | Platform name |
| referrer | VARCHAR(500) | NULL | Referrer URL |
| ip_address | INET | NULL | Share IP address |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Share timestamp |

**Indexes:**
- `idx_share_snippet_id` (snippet_id)
- `idx_share_user_id` (user_id)
- `idx_share_created_at` (created_at DESC)

---

## 5. Organization Models

### 5.1 Tag Model
**Purpose:** Tags for categorizing snippets

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Tag identifier |
| name | VARCHAR(50) | UNIQUE, NOT NULL, LOWERCASE | Tag name (lowercase) |
| slug | VARCHAR(60) | UNIQUE, NOT NULL | URL-friendly slug |
| description | TEXT | NULL, MAX 500 chars | Tag description |
| color | VARCHAR(7) | NULL | Display color (hex) |
| icon | VARCHAR(50) | NULL | Icon name/class |
| usage_count | INTEGER | DEFAULT 0 | Number of times used |
| category | VARCHAR(50) | NULL | Tag category/group |
| is_official | BOOLEAN | DEFAULT FALSE | Official/curated tag |
| created_by | UUID | FOREIGN KEY (users.id), NULL | User who created |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Tag creation timestamp |

**Indexes:**
- `idx_tag_name` (name)
- `idx_tag_slug` (slug)
- `idx_tag_usage_count` (usage_count DESC)

---

### 5.2 SnippetTag Model
**Purpose:** Many-to-many relationship between snippets and tags

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| snippet_id | UUID | FOREIGN KEY (snippets.id) | Reference to snippet |
| tag_id | UUID | FOREIGN KEY (tags.id) | Reference to tag |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Tag assignment timestamp |

**Primary Key:** (snippet_id, tag_id)

**Indexes:**
- `idx_snippet_tag_snippet_id` (snippet_id)
- `idx_snippet_tag_tag_id` (tag_id)

---

### 5.3 Category Model
**Purpose:** Predefined categories for snippets

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Category identifier |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Category name |
| slug | VARCHAR(120) | UNIQUE, NOT NULL | URL-friendly slug |
| description | TEXT | NULL | Category description |
| icon | VARCHAR(50) | NULL | Icon name/class |
| color | VARCHAR(7) | NULL | Display color (hex) |
| parent_category_id | UUID | FOREIGN KEY (categories.id), NULL | Parent category (for subcategories) |
| snippet_count | INTEGER | DEFAULT 0 | Number of snippets in category |
| order | INTEGER | DEFAULT 0 | Display order |
| is_active | BOOLEAN | DEFAULT TRUE | Category active status |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Category creation timestamp |

---

### 5.4 Language Model
**Purpose:** Supported programming languages

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Language identifier |
| name | VARCHAR(50) | UNIQUE, NOT NULL | Language name |
| slug | VARCHAR(60) | UNIQUE, NOT NULL | URL-friendly slug |
| display_name | VARCHAR(100) | NOT NULL | Display name |
| file_extensions | TEXT[] | NOT NULL | Array of file extensions |
| pygments_lexer | VARCHAR(100) | NOT NULL | Pygments lexer name |
| ace_mode | VARCHAR(50) | NULL | ACE editor mode |
| monaco_language | VARCHAR(50) | NULL | Monaco editor language ID |
| icon | VARCHAR(50) | NULL | Icon/logo name |
| color | VARCHAR(7) | NULL | Language color (hex) |
| snippet_count | INTEGER | DEFAULT 0 | Number of snippets |
| popularity_rank | INTEGER | NULL | Popularity ranking |
| is_active | BOOLEAN | DEFAULT TRUE | Language active status |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Language creation timestamp |

---

### 5.5 Collection Model
**Purpose:** User-created collections of snippets

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Collection identifier |
| user_id | UUID | FOREIGN KEY (users.id) | Collection owner |
| name | VARCHAR(255) | NOT NULL | Collection name |
| slug | VARCHAR(300) | NOT NULL | URL-friendly slug |
| description | TEXT | NULL, MAX 1000 chars | Collection description |
| cover_image_url | VARCHAR(500) | NULL | Cover image URL |
| privacy | ENUM | DEFAULT 'public' | 'public', 'private', 'unlisted' |
| snippet_count | INTEGER | DEFAULT 0 | Number of snippets |
| view_count | INTEGER | DEFAULT 0 | Total views |
| favorite_count | INTEGER | DEFAULT 0 | Number of favorites |
| is_featured | BOOLEAN | DEFAULT FALSE | Featured by admin |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Collection creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |
| deleted_at | TIMESTAMP | NULL | Soft delete timestamp |

**Unique Constraint:** (user_id, slug)

**Indexes:**
- `idx_collection_user_id` (user_id)
- `idx_collection_slug` (slug)
- `idx_collection_privacy` (privacy)

---

### 5.6 CollectionSnippet Model
**Purpose:** Snippets within collections

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Collection snippet identifier |
| collection_id | UUID | FOREIGN KEY (collections.id) | Reference to collection |
| snippet_id | UUID | FOREIGN KEY (snippets.id) | Reference to snippet |
| position | INTEGER | DEFAULT 0 | Order within collection |
| note | TEXT | NULL | Personal note about snippet |
| added_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Addition timestamp |

**Unique Constraint:** (collection_id, snippet_id)

---

## 6. Analytics & Tracking Models

### 6.1 SnippetView Model
**Purpose:** Track snippet views

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | View identifier |
| snippet_id | UUID | FOREIGN KEY (snippets.id) | Viewed snippet |
| user_id | UUID | FOREIGN KEY (users.id), NULL | User who viewed (NULL if anonymous) |
| session_id | VARCHAR(255) | NULL | Session identifier |
| ip_address | INET | NULL | Viewer IP address |
| user_agent | TEXT | NULL | Browser user agent |
| referrer | VARCHAR(500) | NULL | Referrer URL |
| device_type | VARCHAR(50) | NULL | Device type |
| browser | VARCHAR(100) | NULL | Browser name |
| os | VARCHAR(100) | NULL | Operating system |
| country | VARCHAR(2) | NULL | Country code (ISO 2-letter) |
| city | VARCHAR(100) | NULL | City name |
| duration_seconds | INTEGER | NULL | Time spent viewing |
| viewed_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | View timestamp |

**Indexes:**
- `idx_snippet_view_snippet_id` (snippet_id)
- `idx_snippet_view_user_id` (user_id)
- `idx_snippet_view_viewed_at` (viewed_at DESC)

---

### 6.2 AnalyticsEvent Model
**Purpose:** Track various analytics events

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Event identifier |
| event_type | VARCHAR(50) | NOT NULL | Event type |
| event_category | VARCHAR(50) | NULL | Event category |
| user_id | UUID | FOREIGN KEY (users.id), NULL | Associated user |
| snippet_id | UUID | FOREIGN KEY (snippets.id), NULL | Associated snippet |
| team_id | UUID | FOREIGN KEY (teams.id), NULL | Associated team |
| metadata | JSONB | NULL | Additional event data |
| ip_address | INET | NULL | Event IP address |
| user_agent | TEXT | NULL | User agent |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Event timestamp |

**Indexes:**
- `idx_analytics_event_type` (event_type)
- `idx_analytics_event_user_id` (user_id)
- `idx_analytics_event_created_at` (created_at DESC)

**Event Types:**
- snippet_created, snippet_viewed, snippet_forked, snippet_favorited
- snippet_shared, snippet_downloaded, snippet_embedded
- user_registered, user_login, user_logout
- comment_created, comment_voted
- team_created, team_joined, team_left
- search_performed, tag_clicked, category_browsed

---

### 6.3 UserActivity Model
**Purpose:** Daily aggregated user activity

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Activity identifier |
| user_id | UUID | FOREIGN KEY (users.id) | Reference to user |
| date | DATE | NOT NULL | Activity date |
| snippets_created | INTEGER | DEFAULT 0 | Snippets created on this day |
| snippets_edited | INTEGER | DEFAULT 0 | Snippets edited |
| snippets_viewed | INTEGER | DEFAULT 0 | Snippets viewed |
| comments_created | INTEGER | DEFAULT 0 | Comments created |
| forks_created | INTEGER | DEFAULT 0 | Forks created |
| favorites_added | INTEGER | DEFAULT 0 | Favorites added |
| contribution_score | INTEGER | DEFAULT 0 | Daily contribution score |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Record creation timestamp |

**Unique Constraint:** (user_id, date)

**Indexes:**
- `idx_user_activity_user_id` (user_id)
- `idx_user_activity_date` (date DESC)

---

### 6.4 SnippetStatistics Model
**Purpose:** Daily aggregated snippet statistics

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Statistics identifier |
| snippet_id | UUID | FOREIGN KEY (snippets.id) | Reference to snippet |
| date | DATE | NOT NULL | Statistics date |
| views | INTEGER | DEFAULT 0 | Views on this day |
| unique_views | INTEGER | DEFAULT 0 | Unique views |
| forks | INTEGER | DEFAULT 0 | Forks created |
| favorites | INTEGER | DEFAULT 0 | Favorites added |
| comments | INTEGER | DEFAULT 0 | Comments created |
| shares | INTEGER | DEFAULT 0 | Shares |
| downloads | INTEGER | DEFAULT 0 | Downloads |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Record creation timestamp |

**Unique Constraint:** (snippet_id, date)

---

## 7. System & Admin Models

### 7.1 AuditLog Model
**Purpose:** System audit trail

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Log identifier |
| user_id | UUID | FOREIGN KEY (users.id), NULL | User who performed action |
| action | VARCHAR(50) | NOT NULL | Action type |
| resource_type | VARCHAR(50) | NOT NULL | Resource type affected |
| resource_id | UUID | NULL | Resource identifier |
| old_values | JSONB | NULL | Previous values |
| new_values | JSONB | NULL | New values |
| ip_address | INET | NULL | Request IP address |
| user_agent | TEXT | NULL | User agent |
| method | VARCHAR(10) | NULL | HTTP method |
| endpoint | VARCHAR(255) | NULL | API endpoint |
| status_code | INTEGER | NULL | HTTP status code |
| error_message | TEXT | NULL | Error message if failed |
| metadata | JSONB | NULL | Additional metadata |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Log creation timestamp |

**Indexes:**
- `idx_audit_log_user_id` (user_id)
- `idx_audit_log_action` (action)
- `idx_audit_log_resource_type` (resource_type)
- `idx_audit_log_created_at` (created_at DESC)

**Action Types:**
- create, read, update, delete, login, logout, suspend, ban, restore
- approve, reject, feature, unfeature, verify, unverify

---

### 7.2 Report Model
**Purpose:** User-generated content reports

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Report identifier |
| reported_by | UUID | FOREIGN KEY (users.id) | User who reported |
| resource_type | ENUM | NOT NULL | 'snippet', 'comment', 'user', 'team' |
| resource_id | UUID | NOT NULL | Reported resource ID |
| reason | ENUM | NOT NULL | Report reason |
| description | TEXT | NULL, MAX 1000 chars | Detailed description |
| status | ENUM | DEFAULT 'pending' | 'pending', 'reviewing', 'resolved', 'dismissed' |
| priority | ENUM | DEFAULT 'medium' | 'low', 'medium', 'high', 'critical' |
| reviewed_by | UUID | FOREIGN KEY (users.id), NULL | Admin who reviewed |
| reviewed_at | TIMESTAMP | NULL | Review timestamp |
| resolution | TEXT | NULL | Resolution notes |
| action_taken | VARCHAR(100) | NULL | Action taken |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Report creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Reason Types:**
- spam, offensive, copyright, inappropriate, malicious_code, other

**Indexes:**
- `idx_report_reported_by` (reported_by)
- `idx_report_resource` (resource_type, resource_id)
- `idx_report_status` (status)
- `idx_report_created_at` (created_at DESC)

---

### 7.3 SystemSetting Model
**Purpose:** Application configuration settings

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Setting identifier |
| key | VARCHAR(100) | UNIQUE, NOT NULL | Setting key |
| value | TEXT | NULL | Setting value |
| value_type | ENUM | NOT NULL | 'string', 'number', 'boolean', 'json' |
| description | TEXT | NULL | Setting description |
| category | VARCHAR(50) | NULL | Setting category |
| is_public | BOOLEAN | DEFAULT FALSE | Visible to non-admins |
| is_editable | BOOLEAN | DEFAULT TRUE | Can be changed via UI |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Setting creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

---

### 7.4 FeatureFlag Model
**Purpose:** Feature toggles

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Flag identifier |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Flag name |
| key | VARCHAR(100) | UNIQUE, NOT NULL | Flag key |
| description | TEXT | NULL | Flag description |
| is_enabled | BOOLEAN | DEFAULT FALSE | Flag status |
| rollout_percentage | INTEGER | DEFAULT 0, MIN 0, MAX 100 | Percentage rollout |
| user_whitelist | UUID[] | NULL | Specific users with access |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Flag creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

---

### 7.5 Announcement Model
**Purpose:** Platform announcements

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Announcement identifier |
| title | VARCHAR(255) | NOT NULL | Announcement title |
| message | TEXT | NOT NULL | Announcement message |
| type | ENUM | DEFAULT 'info' | 'info', 'warning', 'alert', 'success' |
| target_audience | ENUM | DEFAULT 'all' | 'all', 'users', 'teams', 'admins' |
| is_active | BOOLEAN | DEFAULT TRUE | Active status |
| is_dismissible | BOOLEAN | DEFAULT TRUE | Can be dismissed |
| link_url | VARCHAR(500) | NULL | Optional link URL |
| link_text | VARCHAR(100) | NULL | Link text |
| start_date | TIMESTAMP | NULL | Start display date |
| end_date | TIMESTAMP | NULL | End display date |
| created_by | UUID | FOREIGN KEY (users.id) | Admin who created |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Announcement creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

---

## 8. Notification Models

### 8.1 Notification Model
**Purpose:** User notifications

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Notification identifier |
| user_id | UUID | FOREIGN KEY (users.id) | Notification recipient |
| type | VARCHAR(50) | NOT NULL | Notification type |
| title | VARCHAR(255) | NOT NULL | Notification title |
| message | TEXT | NULL | Notification message |
| link | VARCHAR(500) | NULL | Action link |
| icon | VARCHAR(50) | NULL | Icon name |
| actor_id | UUID | FOREIGN KEY (users.id), NULL | User who triggered notification |
| related_resource_type | VARCHAR(50) | NULL | Related resource type |
| related_resource_id | UUID | NULL | Related resource ID |
| is_read | BOOLEAN | DEFAULT FALSE | Read status |
| read_at | TIMESTAMP | NULL | Read timestamp |
| is_sent_email | BOOLEAN | DEFAULT FALSE | Email sent status |
| email_sent_at | TIMESTAMP | NULL | Email sent timestamp |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Notification creation timestamp |

**Notification Types:**
- new_follower, new_comment, comment_reply, snippet_forked
- snippet_featured, team_invitation, team_member_joined
- mention, like, achievement_unlocked

**Indexes:**
- `idx_notification_user_id` (user_id)
- `idx_notification_is_read` (is_read)
- `idx_notification_created_at` (created_at DESC)

---

### 8.2 NotificationPreference Model
**Purpose:** User notification preferences

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Preference identifier |
| user_id | UUID | FOREIGN KEY (users.id), UNIQUE | Reference to user |
| email_new_follower | BOOLEAN | DEFAULT TRUE | Email on new follower |
| email_new_comment | BOOLEAN | DEFAULT TRUE | Email on new comment |
| email_comment_reply | BOOLEAN | DEFAULT TRUE | Email on comment reply |
| email_snippet_forked | BOOLEAN | DEFAULT TRUE | Email on snippet fork |
| email_team_invite | BOOLEAN | DEFAULT TRUE | Email on team invitation |
| email_mention | BOOLEAN | DEFAULT TRUE | Email on mention |
| email_weekly_digest | BOOLEAN | DEFAULT TRUE | Weekly digest email |
| email_marketing | BOOLEAN | DEFAULT FALSE | Marketing emails |
| push_enabled | BOOLEAN | DEFAULT FALSE | Push notifications enabled |
| push_new_follower | BOOLEAN | DEFAULT TRUE | Push on new follower |
| push_new_comment | BOOLEAN | DEFAULT TRUE | Push on new comment |
| push_team_invite | BOOLEAN | DEFAULT TRUE | Push on team invitation |
| in_app_enabled | BOOLEAN | DEFAULT TRUE | In-app notifications |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Preference creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

---

### 8.3 EmailTemplate Model
**Purpose:** Email templates

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Template identifier |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Template name |
| slug | VARCHAR(120) | UNIQUE, NOT NULL | Template slug |
| subject | VARCHAR(255) | NOT NULL | Email subject |
| body_html | TEXT | NOT NULL | HTML body |
| body_text | TEXT | NOT NULL | Plain text body |
| variables | JSONB | NULL | Available variables |
| category | VARCHAR(50) | NULL | Template category |
| is_active | BOOLEAN | DEFAULT TRUE | Template active status |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Template creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

---

### 8.4 EmailLog Model
**Purpose:** Track sent emails

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Log identifier |
| user_id | UUID | FOREIGN KEY (users.id), NULL | Recipient user |
| email | VARCHAR(255) | NOT NULL | Recipient email |
| template_id | UUID | FOREIGN KEY (email_templates.id), NULL | Template used |
| subject | VARCHAR(255) | NOT NULL | Email subject |
| status | ENUM | DEFAULT 'pending' | 'pending', 'sent', 'failed', 'bounced' |
| provider_message_id | VARCHAR(255) | NULL | Email provider message ID |
| error_message | TEXT | NULL | Error if failed |
| opened_at | TIMESTAMP | NULL | Email opened timestamp |
| clicked_at | TIMESTAMP | NULL | Link clicked timestamp |
| sent_at | TIMESTAMP | NULL | Sent timestamp |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Log creation timestamp |

**Indexes:**
- `idx_email_log_user_id` (user_id)
- `idx_email_log_email` (email)
- `idx_email_log_status` (status)
- `idx_email_log_created_at` (created_at DESC)

---

## 9. API & Integration Models

### 9.1 APIKey Model
**Purpose:** API access keys

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | API key identifier |
| user_id | UUID | FOREIGN KEY (users.id) | Key owner |
| name | VARCHAR(100) | NOT NULL | Key name/description |
| key_prefix | VARCHAR(10) | NOT NULL | Key prefix (visible) |
| key_hash | VARCHAR(255) | UNIQUE, NOT NULL | Hashed key |
| permissions | JSONB | NOT NULL | Permissions object |
| scopes | TEXT[] | NOT NULL | API scopes |
| rate_limit | INTEGER | DEFAULT 1000 | Requests per hour |
| is_active | BOOLEAN | DEFAULT TRUE | Key active status |
| last_used_at | TIMESTAMP | NULL | Last usage timestamp |
| last_used_ip | INET | NULL | Last usage IP |
| usage_count | INTEGER | DEFAULT 0 | Total usage count |
| expires_at | TIMESTAMP | NULL | Expiration timestamp |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Key creation timestamp |
| revoked_at | TIMESTAMP | NULL | Revocation timestamp |

**Indexes:**
- `idx_api_key_user_id` (user_id)
- `idx_api_key_key_hash` (key_hash)
- `idx_api_key_is_active` (is_active)

---

### 9.2 APIRequest Model
**Purpose:** API request logs

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Request identifier |
| api_key_id | UUID | FOREIGN KEY (api_keys.id), NULL | API key used |
| user_id | UUID | FOREIGN KEY (users.id), NULL | Authenticated user |
| method | VARCHAR(10) | NOT NULL | HTTP method |
| endpoint | VARCHAR(255) | NOT NULL | API endpoint |
| status_code | INTEGER | NOT NULL | Response status code |
| response_time_ms | INTEGER | NULL | Response time in milliseconds |
| ip_address | INET | NOT NULL | Request IP address |
| user_agent | TEXT | NULL | User agent |
| request_body_size | INTEGER | NULL | Request body size |
| response_body_size | INTEGER | NULL | Response body size |
| error_message | TEXT | NULL | Error if failed |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Request timestamp |

**Indexes:**
- `idx_api_request_api_key_id` (api_key_id)
- `idx_api_request_user_id` (user_id)
- `idx_api_request_created_at` (created_at DESC)

---

### 9.3 Webhook Model
**Purpose:** Webhook configurations

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Webhook identifier |
| user_id | UUID | FOREIGN KEY (users.id) | Webhook owner |
| team_id | UUID | FOREIGN KEY (teams.id), NULL | Team webhook (if applicable) |
| name | VARCHAR(100) | NOT NULL | Webhook name |
| url | VARCHAR(500) | NOT NULL | Webhook URL |
| secret | VARCHAR(255) | NOT NULL | Webhook secret |
| events | TEXT[] | NOT NULL | Subscribed events |
| is_active | BOOLEAN | DEFAULT TRUE | Webhook active status |
| last_triggered_at | TIMESTAMP | NULL | Last trigger timestamp |
| failed_attempts | INTEGER | DEFAULT 0 | Failed delivery count |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Webhook creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

---

### 9.4 WebhookDelivery Model
**Purpose:** Webhook delivery logs

| Field Name | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | UUID | PRIMARY KEY, NOT NULL | Delivery identifier |
| webhook_id | UUID | FOREIGN KEY (webhooks.id) | Reference to webhook |
| event_type | VARCHAR(50) | NOT NULL | Event type |
| payload | JSONB | NOT NULL | Event payload |
| status_code | INTEGER | NULL | HTTP response status |
| response_body | TEXT | NULL | Response body |
| error_message | TEXT | NULL | Error if failed |
| attempts | INTEGER | DEFAULT 1 | Delivery attempts |
| delivered_at | TIMESTAMP | NULL | Successful delivery timestamp |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Delivery attempt timestamp |

---

## 10. Model Relationships Summary

### User Relationships
- User → Snippets (1:many)
- User → Teams (many:many via TeamMember)
- User → Favorites (1:many)
- User → Comments (1:many)
- User → Followers (many:many via Follow)
- User → Collections (1:many)
- User → Notifications (1:many)
- User → AuditLogs (1:many)
- User → APIKeys (1:many)

### Snippet Relationships
- Snippet → User (many:1)
- Snippet → Team (many:1, optional)
- Snippet → Tags (many:many via SnippetTag)
- Snippet → Category (many:1)
- Snippet → Language (many:1)
- Snippet → Versions (1:many via SnippetVersion)
- Snippet → Comments (1:many)
- Snippet → Favorites (1:many)
- Snippet → Views (1:many via SnippetView)
- Snippet → Collections (many:many via CollectionSnippet)
- Snippet → Parent Snippet (many:1, self-referencing for forks)

### Team Relationships
- Team → Users (many:many via TeamMember)
- Team → Snippets (1:many)
- Team → Invitations (1:many via TeamInvitation)
- Team → Roles (1:many via TeamRole)

---

## 11. Total Model Count

| Category | Models | Count |
|----------|--------|-------|
| User Management | User, UserProfile, UserSession, OAuthProvider, PasswordReset, EmailVerification | 6 |
| Snippets | Snippet, SnippetVersion, SnippetFile, SnippetMetadata | 4 |
| Teams | Team, TeamMember, TeamInvitation, TeamRole | 4 |
| Social & Interaction | Favorite, Comment, CommentVote, Follow, Fork, Share | 6 |
| Organization | Tag, SnippetTag, Category, Language, Collection, CollectionSnippet | 6 |
| Analytics & Tracking | SnippetView, AnalyticsEvent, UserActivity, SnippetStatistics | 4 |
| System & Admin | AuditLog, Report, SystemSetting, FeatureFlag, Announcement | 5 |
| Notifications | Notification, NotificationPreference, EmailTemplate, EmailLog | 4 |
| API & Integration | APIKey, APIRequest, Webhook, WebhookDelivery | 4 |
| **TOTAL** | | **43 Models** |

---

## 12. Database Size Estimation

### Storage Estimates (for 100K users, 1M snippets)

| Model | Avg Row Size | Est. Rows | Storage |
|-------|-------------|-----------|---------|
| Users | 2 KB | 100,000 | 200 MB |
| Snippets | 5 KB | 1,000,000 | 5 GB |
| SnippetVersions | 5 KB | 3,000,000 | 15 GB |
| Comments | 500 B | 500,000 | 250 MB |
| SnippetViews | 200 B | 10,000,000 | 2 GB |
| AuditLogs | 1 KB | 5,000,000 | 5 GB |
| AnalyticsEvents | 500 B | 20,000,000 | 10 GB |
| Other Models | - | - | 2 GB |
| **Indexes** | - | - | 10 GB |
| **Total** | | | **~50 GB** |

---

## 13. Key Design Decisions

1. **UUID vs Auto-increment IDs**: Using UUIDs for security and distributed systems
2. **Soft Deletes**: Using `deleted_at` for important records (users, snippets, teams)
3. **Denormalization**: Counter fields (view_count, follower_count) for performance
4. **JSON/JSONB**: For flexible metadata and permissions
5. **Full-text Search**: Using PostgreSQL's built-in FTS for basic search
6. **Partitioning**: Consider partitioning large tables (SnippetViews, AnalyticsEvents, AuditLogs) by date
7. **Archiving**: Move old data to archive tables after 1-2 years

---

## End of Document

This comprehensive data model specification covers all 43 models required for your Code Snippet Sharing Platform with complete field definitions, constraints, relationships, and indexes.