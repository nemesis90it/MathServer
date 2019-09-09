function insertTextAtCursor() {
    if ($0.inputElement.selectionStart || $0.inputElement.selectionStart === 0) {
        var startPos = $0.inputElement.selectionStart;
        var endPos = $0.inputElement.selectionEnd;
        $0.inputElement.value = $0.inputElement.value.substring(0, startPos)
            + $1
            + $0.inputElement.value.substring(endPos, $0.inputElement.value.length);
        $0.inputElement.selectionStart = startPos + $1.length;
        $0.inputElement.selectionEnd = startPos + $1.length;
    } else {
        $0.inputElement.value += $1;
    }
    $0.focus(endPos);
}