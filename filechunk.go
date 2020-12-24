package main

import (
	"fmt"
	"strconv"
	"strings"

	"github.com/google/uuid"
)

// FileChunk is a sliced up piece of a file fitting some size.
type FileChunk struct {
	name string
	uuid string

	part  int32
	total int32

	base64Chunk string // Up to `chunkLimit`
}

// Marshal serializes a FileChunk into a string which can be QR encoded.
func (f *FileChunk) Marshal() string {
	return strings.Join([]string{
		f.name,
		f.uuid,
		fmt.Sprintf("%08X", f.part),
		fmt.Sprintf("%08X", f.total),
		f.base64Chunk,
	}, ":")
}

// IsFileChunk determines if the parsed string from a QR code is a FileChunk.
func IsFileChunk(s string) bool {
	parts := strings.Split(s, ":")
	if len(parts) != 5 { // name, uuid, part, total, chunk
		return false
	}
	if len(parts[0]) == 0 {
		return false
	}
	if _, err := uuid.Parse(parts[1]); err != nil {
		return false
	}
	if _, err := strconv.ParseInt(parts[2], 16, 32); err != nil {
		return false
	}
	if _, err := strconv.ParseInt(parts[3], 16, 32); err != nil {
		return false
	}
	if len(parts[4]) == 0 {
		return false
	}

	return true
}

// ParseChunk parses the string from a QR code into a file chunk.
func ParseChunk(s string) *FileChunk {
	if !IsFileChunk(s) {
		return nil
	}
	parts := strings.Split(s, ":")
	return &FileChunk{
		name: parts[0],
	}
}
