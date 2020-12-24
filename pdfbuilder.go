package main

import (
	"flag"
	"fmt"

	"github.com/jung-kurt/gofpdf"
)

var (
	pageSize         = flag.String("page_size", "letter", "Size of each page in the output PDF. Defaults to 'letter'. See https://godoc.org/github.com/jung-kurt/gofpdf#pkg-constants for supported values.")
	pageMarginInches = flag.Float64("page_margin", 0.25, "Margin around of the border of each page to not render QR codes.")
	qrCodesPerRow    = flag.Int("qr_codes_per_row", 2, "Number of QR codes to fit per row. The QR code will stretch to fit the row width. The number of rows will depend on the height of each row.")
	pageTitle        = flag.String("page_title", "A file for you", "String to be printed at the top of every page.")
)

type pos struct {
	x, y, w, h float64
}

func generatePositions(width, height float64) []*pos {
	/* Adjust for margin */
	width -= 2 * *pageMarginInches
	height -= 2 * *pageMarginInches

	sideLength := width / float64(*qrCodesPerRow)
	rows := int(height / sideLength)
	heightCenteringMargin := (height - (float64(rows) * sideLength)) / 2.0 // Remainder div 2.

	var out []*pos
	var i, j int
	for i < rows {
		for j < *qrCodesPerRow {
			out = append(out, &pos{sideLength * float64(j), sideLength * float64(i), sideLength, sideLength})
			j++
		}
		j = 0
		i++
	}

	// The generated positions are relative to 0,0. Adjust them by adding in the margin(s).
	for _, p := range out {
		p.x += *pageMarginInches
		p.y += *pageMarginInches + heightCenteringMargin
	}

	return out

}

func imagesToPDF(imagePaths []string) (*gofpdf.Fpdf, error) {
	pdf := gofpdf.New("P", "in", *pageSize, "")

	positions := generatePositions(pdf.GetPageSize())
	var opt gofpdf.ImageOptions
	opt.ImageType = "png"

	posCount := len(positions)
	for _, imgPath := range imagePaths {
		if posCount == len(positions) {
			pdf.AddPage()
			pdf.SetFont("Arial", "", 11)
			pdf.Write(0.15, fmt.Sprintf("%s -- Page %d", *pageTitle, pdf.PageCount()))
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
