-- PostgreSQL Schema for Chat Application

-- Enable UUID extension (if not already enabled)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users Table
CREATE TABLE public.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Chat Rooms Table
CREATE TABLE public.chat_rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    is_private BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Chat Room Members Table
CREATE TABLE public.chat_room_members (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   chat_room_id UUID REFERENCES chat_rooms(id) ON DELETE CASCADE,
   user_id UUID REFERENCES users(id) ON DELETE CASCADE,
   joined_at TIMESTAMP DEFAULT NOW(),
   role VARCHAR(20) CHECK (role IN ('admin', 'member')) DEFAULT 'member',
   UNIQUE (chat_room_id, user_id)
);

-- Messages Table
CREATE TABLE public.messages (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  chat_room_id UUID REFERENCES chat_rooms(id) ON DELETE CASCADE,
  sender_id UUID REFERENCES users(id) ON DELETE SET NULL,
  message TEXT NOT NULL,
  file_url TEXT, -- If the message contains a file
  encrypted BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

-- Indexes for faster search
CREATE INDEX idx_messages_chat_room_id ON messages(chat_room_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_created_at ON messages(created_at DESC);

-- Message Reactions Table
CREATE TABLE public.message_reactions (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   message_id UUID REFERENCES messages(id) ON DELETE CASCADE,
   user_id UUID REFERENCES users(id) ON DELETE CASCADE,
   reaction VARCHAR(50) NOT NULL, -- Example: "👍", "❤️", "😂"
   created_at TIMESTAMP DEFAULT NOW(),
   UNIQUE (message_id, user_id, reaction) -- Prevent duplicate reactions
);

-- Message Search Index (Full-Text Search)
ALTER TABLE messages ADD COLUMN search_vector tsvector
    GENERATED ALWAYS AS (to_tsvector('english', message)) STORED;
CREATE INDEX idx_messages_search ON messages USING gin(search_vector);

-- File Storage Table
CREATE TABLE public.files (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   chat_room_id UUID REFERENCES chat_rooms(id) ON DELETE CASCADE,
   sender_id UUID REFERENCES users(id) ON DELETE SET NULL,
   file_name TEXT NOT NULL,
   file_url TEXT NOT NULL,
   file_type VARCHAR(50),
   created_at TIMESTAMP DEFAULT NOW()
);

-- Custom Branding Table
CREATE TABLE public.branding (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  theme_color VARCHAR(7) DEFAULT '#ffffff', -- Hex color codes
  logo_url TEXT,
  created_at TIMESTAMP DEFAULT NOW()
);
