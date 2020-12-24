package main

import (
	"fmt"

	"github.com/jung-kurt/gofpdf"
)

type pos struct {
	x, y, w, h float64
}

func imagesToPDF(imagePaths []string) (*gofpdf.Fpdf, error) {
	pdf := gofpdf.New("P", "in", "letter", "")
	positions := []pos{
		{0.25, 1.5, 4, 4},
		{4.25, 1.5, 4, 4},
		{0.25, 5.5, 4, 4},
		{4.25, 5.5, 4, 4},
	}

	var opt gofpdf.ImageOptions
	opt.ImageType = "png"

	posCount := len(positions)
	for _, imgPath := range imagePaths {
		if posCount == len(positions) {
			pdf.AddPage()
			pdf.SetFont("Arial", "", 11)
			pdf.Write(0.5, fmt.Sprintf("Page %d", pdf.PageCount()))
			posCount = 0
		}
		position := positions[posCount]
		posCount++
		pdf.ImageOptions(
			imgPath,
			position.x,
			position.y,
			position.w,
			position.h,
			false, opt, 0, "")
	}
	return pdf, nil
}
