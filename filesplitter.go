package main

import (
	"encoding/base64"
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"

	"github.com/google/uuid"
)

// Split creates a series of FileChunk objects from a file f.
func Split(f *os.File, qrSizeLimit int) ([]*FileChunk, error) {
	fi, err := f.Stat()
	if err != nil {
		return nil, err
	}
	if fi.IsDir() {
		return nil, fmt.Errorf("cannot split a directory")
	}

	fbytes, err := ioutil.ReadAll(f)
	if err != nil {
		return nil, err
	}

	var out []*FileChunk

	name := filepath.Base(f.Name())
	id := uuid.New()
	chunkLimit := qrSizeLimit - metadataLength(name, id.String())
	fenc := base64.StdEncoding.EncodeToString(fbytes)
	var part int32
	total := int32(len(fenc) / chunkLimit)
	for true {
		if len(fenc) < chunkLimit {
			out = append(out, &FileChunk{
				name:        name,
				uuid:        id.String(),
				part:        part,
				total:       total,
				base64Chunk: fenc,
			})
			break
		}
		out = append(out, &FileChunk{
			name:        name,
			uuid:        id.String(),
			part:        part,
			total:       total,
			base64Chunk: fenc[:chunkLimit],
		})
		fenc = fenc[chunkLimit:]
		part++
	}

	return out, nil
}

func metadataLength(filename, id string) int {
	return (len(filename) +
		1 + // : separator
		len(id) +
		1 + // : separator
		8 + // MaxInt32 hex
		1 + // : separator
		8 + // MaxInt32 hex
		1) // : separator

}
