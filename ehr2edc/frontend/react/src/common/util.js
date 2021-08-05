
export function pick(obj, ...names) {
    let result = {};
    let idx = 0;
    while (idx < names.length) {
        const name = names[idx];
        if (name in obj) {
            result[name] = obj[name];
        }
        idx += 1;
    }
    return result;
}
