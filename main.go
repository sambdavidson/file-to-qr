package main

import (
	"fmt"
	"log"
	"os"
	"path/filepath"

	"github.com/skip2/go-qrcode"
)

func main() {
	f, err := os.Open("./test_data/christmas-wishes.jpg")
	if err != nil {
		log.Fatal(err)
	}
	o, err := Split(f, 2953)

	if err != nil {
		log.Fatal(err)
	}

	log.Printf("Number of QR codes: %d\n", len(o))

	var imagePaths []string
	os.MkdirAll("./tmp", os.ModeDir)
	for i, chunk := range o {
		qr, err := qrcode.New(chunk.Marshal(), qrcode.Low)
		if err != nil {
			log.Fatal(err)
		}
		path := fmt.Sprintf("./tmp/%s_qr_%d.png", chunk.uuid[:8], i)
		err = qr.WriteFile(-1, path)
		if err != nil {
			log.Fatal(err)
		}
		imagePaths = append(imagePaths, path)
	}
	pdf, err := imagesToPDF(imagePaths)
	if err != nil {
		log.Fatal(err)
	}

	log.Printf("Number of PDF pages: %d\n", pdf.PageCount())

	os.MkdirAll("./out", os.ModeDir)
	err = pdf.OutputFileAndClose(fmt.Sprintf("./out/chunked_%s.pdf", filepath.Base(f.Name())))
	if err != nil {
		log.Fatal(err)
	}

}
